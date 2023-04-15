package com.rijaldev.snapgram.presentation.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentHomeBinding
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.presentation.adapter.StoryAdapter
import com.rijaldev.snapgram.presentation.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.getStories()
        }

        showRefreshing(true)
        viewModel.getStories()
        viewModel.stories.observe(viewLifecycleOwner, storyObserver)
    }

    private fun setUpRecyclerView() {
        storyAdapter = StoryAdapter { id, ivStory, tvName ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(ivStory, getString(R.string.story_image)),
                Pair(tvName, getString(R.string.story_name))
            ).toBundle()

            val intent = Intent(requireActivity(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_ID, id)
            }

            requireActivity().startActivity(intent, options)
        }

        binding?.rvStory?.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private val storyObserver = Observer<Result<List<Story>?>> { result ->
        when (result) {
            is Result.Loading -> {}
            is Result.Success -> {
                showRefreshing(false)
                val stories = result.data
                storyAdapter.submitList(stories)
                if (stories.isNullOrEmpty()) showError(getString(R.string.empty))
            }
            is Result.Error -> {
                showRefreshing(false)
                showError(result.message)
            }
        }
    }

    private fun showRefreshing(isRefreshing: Boolean) {
        binding?.apply {
            swipeRefresh.isRefreshing = isRefreshing
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
}