package com.dh.myapplication.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.dh.myapplication.core.data.UserAddress
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.*

class Location(val context: Context) {

    companion object {
        // Companion object containing a constant TAG for logging
        private const val TAG = "getLocation"
    }
    // MutableStateFlow to hold the user's address information
    var _address = MutableStateFlow<UserAddress>(UserAddress())

    // Function to get the user's location and update the address information
    @SuppressLint("MissingPermission")
    suspend fun getLocationMain(fusedLocationClient: FusedLocationProviderClient) {


        // First Step: Get the current location
        val location = getLocation(fusedLocationClient) ?: return

        // Update Results with current time and date
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _address.update { it.copy(lan = location.latitude, lon = location.longitude, time = currentTime, date = currentDate) }




        // Get user address from latitude and longitude
        val result: Address? = getAddressFromLocation(context, location.latitude, location.longitude)
        if (result != null) {
            // Update user location details
            getUserLocation(result)
        }
    }

    // Function to retrieve the last known location
    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    suspend fun getLocation(fusedLocationClient: FusedLocationProviderClient): Location? {

        return suspendCancellableCoroutine { continuation ->

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                if (location != null) {
                    // Resume coroutine with location if available
                    continuation.resume(location, null)
                } else {
                    // Resume coroutine with null if location is not available
                    continuation.resume(null, null)
                }

            }

        }

    }




    // Function to update user location details based on the retrieved address
    fun getUserLocation(reesult: Address) {

        val city = reesult.locality
        val state = reesult.adminArea
        val country = reesult.countryName

        _address.update { it.copy(city, state, country) }


    }

    // Function to get an address from latitude and longitude
    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        if (addresses != null && addresses.isNotEmpty()) {
            return addresses[0]
        }
        return null
    }


}
