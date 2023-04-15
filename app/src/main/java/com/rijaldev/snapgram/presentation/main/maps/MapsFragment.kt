package com.rijaldev.snapgram.presentation.main.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentMapsBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.Story
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<MapsViewModel>()

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.run {
            isCompassEnabled = true
        }

        fetchStories()
        setMapStyle()
    }

    private fun fetchStories() {
        viewModel.getStoryWithLocation()
        viewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> showMarker(result.data)
                is Result.Error -> showError(result.message)
            }
        }
    }

    private fun setMapStyle() {
        try {
            val isSuccess = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style)
            )
            if (!isSuccess) Log.e(TAG, "setMapStyle: Parsing Failed.")
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

    private fun showMarker(stories: List<Story>) {
        val firstStory = stories.firstOrNull() ?: return
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(firstStory.lat ?: return,  firstStory.lon ?: return), 10f
        )
        map.animateCamera(cameraUpdate)

        stories.forEach { story ->
            val latLng = LatLng(story.lat ?: return, story.lon ?: return)

            viewLifecycleOwner.lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(story.photoUrl)
                        .override(100, 100)
                        .circleCrop()
                        .submit()
                        .get()
                }

                map.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .position(latLng)
                        .title(story.name)
                )
            }
        }
    }

    private fun showError(message: String?) {
        Snackbar.make(
            requireActivity().window.decorView,
            message.toString(),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MapsFragment"
    }
}