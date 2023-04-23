package com.rijaldev.snapgram.presentation.addstory.upload

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentUploadBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.StoryUpload
import com.rijaldev.snapgram.util.EspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<UploadViewModel>()
    private val navArgs by navArgs<UploadFragmentArgs>()
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private lateinit var locationRequest: LocationRequest
    private var location: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
    }

    private fun setUpView() {
        binding?.apply {
            val imageResult = navArgs.imageResult

            Glide.with(requireActivity())
                .load(if (imageResult.isFromCamera) imageResult.imageBitmap else imageResult.imageUri)
                .into(ivStory)

            btnClose.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            buttonAdd.setOnClickListener {
                val image = imageResult.imageFile
                val description = edAddDescription.text.toString().trim()

                if (image == null || description.isEmpty()) {
                    Toast.makeText(requireActivity(),
                        getString(R.string.data_empty), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showLoading(true)
                compressThenUploadImage(image, description, location)
            }
            switchGps.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLastLocation()
                    createLocationRequest()
                } else location = null
            }

            viewModel.uploadStoryResult.observe(viewLifecycleOwner, uploadObserver)
        }
    }

    private fun compressThenUploadImage(image: File, description: String, location: Location?) {
        EspressoIdlingResource.increment()
        viewLifecycleOwner.lifecycleScope.launch {
            val compressedFile = Compressor.compress(requireActivity(), image) {
                quality(50)
                size(1_000_000)
            }
            EspressoIdlingResource.decrement()
            viewModel.uploadStory(compressedFile, description, location)
        }
    }

    private val uploadObserver = Observer<Result<StoryUpload>> { result ->
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                showSnackBar(result.data.message)

                activity?.run {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            is Result.Error -> {
                showLoading(false)
                showSnackBar(result.message)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            pbCircle.isVisible = isLoading
        }
    }

    private fun showSnackBar(message: String?) {
        Snackbar.make(
            requireActivity().window.decorView,
            message.toString(),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
        }
    }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> showSnackBar("Wait...")
            Activity.RESULT_CANCELED -> showSnackBar("You must turn on the GPS!")
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(1).apply {
            setIntervalMillis(TimeUnit.SECONDS.toMillis(1))
            setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(1))
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())

        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        showSnackBar(e.message)
                    }
                }
            }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            and checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                }
                else showSnackBar(getString(R.string.location_not_found_error))
            }
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}