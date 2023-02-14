package com.example.mylocationassignment

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mylocationassignment.databinding.FragmentMapsBinding
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
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate( inflater, container, false)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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

        setUpCurrentLocation(map)

        map.uiSettings.apply{
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = false
        }
        map.isMyLocationEnabled = true
        map.setMinZoomPreference(12f)
    }

    @SuppressLint("MissingPermission")
    private fun setUpCurrentLocation(map: GoogleMap) {

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

            }

            map.setOnMarkerClickListener(this)
        }
    }

    override fun onMarkerClick(p0: Marker) = false

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}