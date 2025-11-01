    package com.example.spotfinder

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Delete
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.google.android.gms.maps.CameraUpdateFactory
    import com.example.spotfinder.data.LocationEntity
    import com.example.spotfinder.ui.theme.SpotFinderTheme
    import com.example.spotfinder.viewmodel.MainViewModel
    import com.google.android.gms.maps.model.CameraPosition
    import com.google.android.gms.maps.model.LatLng
    import com.google.maps.android.compose.GoogleMap
    import com.google.maps.android.compose.Marker
    import com.google.maps.android.compose.MarkerState
    import com.google.maps.android.compose.rememberCameraPositionState

    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Initialize the Google Places API once
            if (!com.google.android.libraries.places.api.Places.isInitialized()) {
                com.google.android.libraries.places.api.Places.initialize(applicationContext, getString(R.string.google_maps_key))
            }

            enableEdgeToEdge()
            setContent {
                SpotFinderTheme {
                    val mainViewModel: MainViewModel = viewModel()
                    MainScreen(viewModel = mainViewModel)
                }
            }
        }
    }


    @Composable
    fun MainScreen(viewModel: MainViewModel) {
        // 3. Collect state from the ViewModel. Compose will automatically update the UI when this changes.
        val locations by viewModel.locations.collectAsState()
        val foundLocation by viewModel.found.collectAsState()

        // Internal state for the search bar's text
        var searchQuery by remember { mutableStateOf("") }

        // Set a default camera position to the GTA (Toronto)
        val defaultLocation = LatLng(43.6532, -79.3832)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
        }

           LaunchedEffect(foundLocation) {
            foundLocation?.let {
                val position = LatLng(it.latitude, it.longitude)
                // Use the imported CameraUpdateFactory directly
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition( // No more red here
                        CameraPosition.fromLatLngZoom(position, 15f)
                    )
                )
            }
        }


        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // --- Search UI ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search for a place") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.searchLocation(searchQuery) }) {
                        Text("Search")
                    }
                }

                // --- Google Map UI ---
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Map takes up available space
                    cameraPositionState = cameraPositionState
                ) {
                    // Display markers for all saved locations
                    locations.forEach { location ->
                        Marker(
                            state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                            title = location.address
                        )
                    }
                    // Display a special marker for the most recently searched location
                    foundLocation?.let {
                        Marker(
                            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                            title = it.address,
                            snippet = "Searched Location"
                        )
                    }
                }

    // --- 'Add Location' Button ---
                foundLocation?.let { locationToAdd ->
                    Button(
                        onClick = {
                            viewModel.addLocation(locationToAdd)
                            // Replace this line:
                            // viewModel.searchLocation("")

                            // With this much clearer line:
                            viewModel.clearFound()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Add '${locationToAdd.address}' to Saved Locations")
                    }
                }


                // --- Saved Locations List ---
                Text(
                    "Saved Locations",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // List takes up available space
                ) {
                    items(locations) { location ->
                        LocationItem(location = location, onDelete = { viewModel.deleteLocation(location) })
                    }
                }
            }
        }
    }

    @Composable
    fun LocationItem(location: LocationEntity, onDelete: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = location.address, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Location", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

