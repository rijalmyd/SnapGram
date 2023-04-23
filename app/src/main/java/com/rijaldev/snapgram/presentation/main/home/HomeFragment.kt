package com.rijaldev.snapgram.presentation.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.snackbar.Snackbar
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.FragmentHomeBinding
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.presentation.adapter.LoadingStateAdapter
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

        viewModel.stories.observe(viewLifecycleOwner, storyObserver)
    }

    private fun setUpRecyclerView() {
        storyAdapter = StoryAdapter { id, ivStory, tvName ->
            handleMovingPage(id, ivStory, tvName)
        }
        val footerAdapter = LoadingStateAdapter {
            storyAdapter.retry()
        }
        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == storyAdapter.itemCount && footerAdapter.itemCount > 0) 2
                else 1
            }
        }

        storyAdapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && storyAdapter.itemCount < 1) {
                showSnackBar(getString(R.string.empty))
            }
        }

        binding?.apply {
            swipeRefresh.setOnRefreshListener {
                storyAdapter.refresh()
            }
            rvStory.apply {
                layoutManager = gridLayoutManager
                setHasFixedSize(true)
                adapter = storyAdapter.withLoadStateFooter(footerAdapter)
            }
            btnScrollToTop.setOnClickListener {
                rvStory.smoothScrollToPosition(0)
            }
            rvStory.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    btnScrollToTop.isVisible = dy < 0 && recyclerView.isNotAtTop()
                }
            })
        }
    }

    private fun RecyclerView.isNotAtTop() = canScrollVertically(-1)

    private fun handleMovingPage(id: String?, ivStory: ImageView?, tvName: TextView?) {
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

    private val storyObserver = Observer<PagingData<Story>> { result ->
        storyAdapter.submitData(lifecycle, result)
        hideRefreshing()
    }

    private fun hideRefreshing() {
        binding?.apply {
            swipeRefresh.isRefreshing = false
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