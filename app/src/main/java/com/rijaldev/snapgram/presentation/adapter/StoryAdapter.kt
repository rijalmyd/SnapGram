package com.rijaldev.snapgram.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.ItemStoryBinding
import com.rijaldev.snapgram.domain.model.story.Story

class StoryAdapter(
    private val onItemClicked: (id: String?, ivStory: ImageView?, tvName: TextView?) -> Unit
) : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story?) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story?.photoUrl)
                    .placeholder(R.drawable.bg_image_loading)
                    .into(ivItemPhoto)
                tvItemName.text = story?.name

                itemView.setOnClickListener {
                    onItemClicked(story?.id, ivItemPhoto, tvItemName)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story) =
                oldItem == newItem
        }
    }
}