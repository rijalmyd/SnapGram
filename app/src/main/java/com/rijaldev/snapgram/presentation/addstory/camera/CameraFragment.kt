package com.rijaldev.snapgram.presentation.addstory.camera

import android.Manifest
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentCameraBinding
import com.rijaldev.snapgram.presentation.addstory.upload.model.ImageResult
import com.rijaldev.snapgram.util.createFile
import com.rijaldev.snapgram.util.rotateBitmap
import com.rijaldev.snapgram.util.toBitmap
import com.rijaldev.snapgram.util.toFile

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    private lateinit var soundPool: SoundPool
    private var soundId = 0
    private var soundPoolLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        launcherPermission.launch(PERMISSIONS.first())

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) soundPoolLoaded = true
        }
        soundId = soundPool.load(requireActivity(), R.raw.shutter_camera, 1)

        setUpView()
    }

    private fun setUpView() {
        binding?.apply {
            btnCapture.setOnClickListener {
                if (soundPoolLoaded) {
                    soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                }
                takePhoto()
            }
            btnFlipCamera.setOnClickListener {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
            btnAddGallery.setOnClickListener {
                val mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                launcherGallery.launch(PickVisualMediaRequest(mediaType))
            }
            btnClose.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.open_camera_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(requireActivity().application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val imageBitmap = photoFile.toBitmap()
                    val rotatedBitmap = imageBitmap.rotateBitmap(
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    moveToUpload(ImageResult(photoFile, imageBitmap = rotatedBitmap))
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.taking_photo_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun moveToUpload(imageResult: ImageResult) {
        val toUpload = CameraFragmentDirections.actionCameraFragmentToUploadFragment(imageResult)
        findNavController().navigate(toUpload)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val imageFile = it.toFile(requireActivity())
            moveToUpload(ImageResult(imageFile, it, isFromCamera = false))
        }
    }

    private val launcherPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted and !isAllPermissionsGranted()) {
            Toast.makeText(requireActivity(), getString(R.string.access_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAllPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
        _binding = null
    }

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}