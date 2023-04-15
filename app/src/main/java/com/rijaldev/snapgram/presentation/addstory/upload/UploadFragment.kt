package com.rijaldev.snapgram.presentation.addstory.upload

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentUploadBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.StoryUpload
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<UploadViewModel>()
    private val navArgs by navArgs<UploadFragmentArgs>()

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
                compressThenUploadImage(image, description)
            }

            viewModel.uploadStoryResult.observe(viewLifecycleOwner, uploadObserver)
        }
    }

    private fun compressThenUploadImage(image: File, description: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val compressedFile = Compressor.compress(requireActivity(), image) {
                quality(50)
                size(1_000_000)
            }
            viewModel.uploadStory(compressedFile, description)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}