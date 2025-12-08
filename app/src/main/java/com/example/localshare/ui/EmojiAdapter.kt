package com.example.localshare.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.localshare.data.EmojiEntity
import com.example.localshare.databinding.ItemEmojiBinding
import java.io.File

class EmojiAdapter (
        private val onItemClick: (EmojiEntity) -> Unit
        ):ListAdapter<EmojiEntity, EmojiAdapter.EmojiViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<EmojiEntity>() {
        override fun areItemsTheSame(oldItem: EmojiEntity, newItem: EmojiEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EmojiEntity, newItem: EmojiEntity): Boolean {
            return oldItem == newItem
        }
    }

    inner class EmojiViewHolder(private val binding: ItemEmojiBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            binding.root.setOnClickListener {
                onItemClick(getItem(bindingAdapterPosition))
            }
        }
        fun bind(item:EmojiEntity) {
            val file = File(item.filePath)

            Glide.with(binding.imageEmoji.context)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(binding.imageEmoji)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = ItemEmojiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}