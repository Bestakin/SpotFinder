// C:/Users/akinl/AndroidStudioProjects/SpotFinder/app/src/main/java/com/example/spotfinder/data/Repository.kt

package com.example.spotfinder.data

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Add a context parameter to your Repository's constructor
class Repository(private val locationDao: LocationDao, private val context: Context) {

    // Create the client once
    private val placesClient = Places.createClient(context)

    // --- KEEP YOUR EXISTING DAO FUNCTIONS ---
    suspend fun getAllLocations(): List<LocationEntity> {
        return withContext(Dispatchers.IO) {
            locationDao.getAllLocations()
        }
    }

    suspend fun addLocation(location: LocationEntity) {
        withContext(Dispatchers.IO) {
            locationDao.addLocation(location)
        }
    }

    suspend fun updateLocation(location: LocationEntity) {
        withContext(Dispatchers.IO) {
            locationDao.updateLocation(location)
        }
    }

    suspend fun deleteLocation(location: LocationEntity) {
        withContext(Dispatchers.IO) {
            locationDao.deleteLocation(location)
        }
    }

    // --- ADD THE NEW SEARCH FUNCTION ---
    suspend fun searchLocation(query: String): LocationEntity? {
        // Use suspendCancellableCoroutine to convert the callback-based API to a suspending function
        return suspendCancellableCoroutine { continuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val prediction = response.autocompletePredictions.firstOrNull()
                if (prediction == null) {
                    continuation.resume(null) // No prediction found
                    return@addOnSuccessListener
                }

                val placeId = prediction.placeId
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                val detailsRequest = FetchPlaceRequest.newInstance(placeId, placeFields)

                placesClient.fetchPlace(detailsRequest).addOnSuccessListener { fetchPlaceResponse ->
                    val place = fetchPlaceResponse.place
                    val latLng = place.latLng
                    if (latLng != null && place.address != null) {
                        val locationEntity = LocationEntity(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            address = place.address!!
                        )
                        continuation.resume(locationEntity) // Success!
                    } else {
                        continuation.resume(null) // Found place but it has no coordinates
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception) // Fetching details failed
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception) // Autocomplete failed
            }
        }
    }
}
