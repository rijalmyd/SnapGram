package com.rijaldev.snapgram.presentation.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.ActivityDetailBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.presentation.auth.AuthActivity
import com.rijaldev.snapgram.util.getTimeAgoFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Restarting", "onCreate: g")

        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val storyId = intent.getStringExtra(EXTRA_ID)
        storyId?.run(viewModel::setStoryId)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.detailStory.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val detail = result.data
                    detail.populateDetail()
                }
                is Result.Error -> {
                    showLoading(false)
                    val message = result.message ?: ""
                    if (message.contains("401")) {
                        Toast.makeText(
                            this,
                            getString(R.string.sign_in_first),
                            Toast.LENGTH_SHORT
                        ).show()
                        moveToAuth()
                        return@observe
                    }
                    Toast.makeText(
                        this,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun Story.populateDetail() {
        with(binding) {
            toolbar.title = name
            toolbar.subtitle = createdAt.getTimeAgoFormat()
            tvDetailDescription.text = description
            Glide.with(this@DetailActivity)
                .load(photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDetailPhoto)
        }
    }

    private fun moveToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.isVisible = isLoading
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ID = "story_id"
    }
}