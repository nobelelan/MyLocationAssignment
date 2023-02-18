package com.example.mylocationassignment

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mylocationassignment.api.RetrofitInstance
import com.example.mylocationassignment.databinding.FragmentMapsBinding
import com.example.mylocationassignment.model.PlacesResponse
import com.example.mylocationassignment.utils.Constants.API_KEY
import com.example.mylocationassignment.utils.Constants.NEARBY_RADIUS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate( inflater, container, false)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!Places.isInitialized()){
            Places.initialize(requireContext(), API_KEY, Locale.getDefault())
        }
        placesClient = Places.createClient(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        binding.imgAtm.setOnClickListener {
            setUpCurrentLocation(map, "atm")
        }
        binding.imgHospital.setOnClickListener {
            setUpCurrentLocation(map, "hospital")
        }
        binding.imgMosque.setOnClickListener {
            setUpCurrentLocation(map, "mosque")
        }


        map.uiSettings.apply{
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = false
        }
        map.isMyLocationEnabled = true
        map.setMinZoomPreference(12f)
    }

    @SuppressLint("MissingPermission")
    private fun setUpCurrentLocation(map: GoogleMap, type: String) {

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
            if (task.result != null){
                val lat = task.result.latitude
                val lng = task.result.longitude
                val lastKnownLocation = LatLng(lat, lng)

                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (addresses != null && addresses.size > 0){
                    var address = ""
                    if (addresses.first().subThoroughfare != null){
                        address += "${addresses.first().subThoroughfare}, "
                    }
                    if (addresses.first().thoroughfare != null){
                        address += "${addresses.first().thoroughfare}, "
                    }
                    if (addresses.first().locality != null){
                        address += addresses.first().locality
                    }
                    map.addMarker(MarkerOptions().position(lastKnownLocation).title(address))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 15f))
                }

                getNearByPlaces("$lat,$lng", type)

            }

            map.setOnMarkerClickListener(this)
        }
    }

    private fun getNearByPlaces(location: String, type: String) {
        RetrofitInstance.placesApi.getNearbyPlaces(location, NEARBY_RADIUS, type, API_KEY)
            .enqueue(object : Callback<PlacesResponse> {
                override fun onResponse(
                    call: Call<PlacesResponse>,
                    response: Response<PlacesResponse>
                ) {
                    val placesResponse = response.body()
                    if (placesResponse != null && placesResponse.status == "OK"){
                        for (place in placesResponse.results){
                            val position = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                            map.addMarker(MarkerOptions().position(position).title(place.name))
                        }
//                        Toast.makeText(requireContext(), "wow", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(requireContext(), placesResponse?.status, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to fetch!", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onMarkerClick(p0: Marker) = false

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}