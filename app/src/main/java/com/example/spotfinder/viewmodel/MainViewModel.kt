package com.example.spotfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotfinder.data.AppDatabase
import com.example.spotfinder.data.LocationEntity
import com.example.spotfinder.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Database and Repository setup
    private val dao = AppDatabase.getDatabase(application).locationDao()
    private val repository = Repository(dao, application.applicationContext)


    // StateFlow to hold the list of all saved locations
    private val _locations = MutableStateFlow<List<LocationEntity>>(emptyList())
    val locations: StateFlow<List<LocationEntity>> = _locations.asStateFlow()

    // StateFlow to hold the result of a single location search
    private val _found = MutableStateFlow<LocationEntity?>(null)
    val found: StateFlow<LocationEntity?> = _found.asStateFlow()

    init {
        loadAllLocations()
    }

    fun clearFound() {
        _found.value = null
    }

    /**
     * Fetches all locations from the repository and updates the `_locations` StateFlow.
     */
    private fun loadAllLocations() {
        viewModelScope.launch {
            _locations.value = repository.getAllLocations()
        }
    }

    /**
     * Searches for a location by its address and updates the `_found` StateFlow.
     */
    fun searchLocation(query: String) {
        viewModelScope.launch {
            _found.value = repository.searchLocation(query)
        }
    }

    /**
     * Adds a new location to the database
     */
    fun addLocation(location: LocationEntity) {
        viewModelScope.launch {
            repository.addLocation(location)
            loadAllLocations() // Refresh the list
        }
    }

    /**
     * Updates an existing location
     */
    fun updateLocation(location: LocationEntity) {
        viewModelScope.launch {
            repository.updateLocation(location)
            loadAllLocations() // Refresh the list
        }
    }

    fun deleteLocation(location: LocationEntity) {
        viewModelScope.launch {
            repository.deleteLocation(location)
            loadAllLocations() // Refresh the list
        }
    }

}
