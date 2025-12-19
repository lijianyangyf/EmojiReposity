package com.example.localshare.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.localshare.data.EmojiEntity
import com.example.localshare.databinding.ItemEmojiBinding
import kotlinx.coroutines.selects.select
import java.io.File

class EmojiAdapter (
        private val onItemClick: (EmojiEntity) -> Unit,
        private val onLongClick: () ->Unit
        ):ListAdapter<EmojiEntity, EmojiAdapter.EmojiViewHolder>(DiffCallback) {
    private val selectedItems = HashSet<Long>()

    var isEditMode = false
        set(value) {
            field = value
            if (!value) {
                selectedItems.clear()
            }
            notifyDataSetChanged()
        }

    fun getSelectedList(): List<EmojiEntity> {
        return currentList.filter { selectedItems.contains(it.id) }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<EmojiEntity>() {
        override fun areItemsTheSame(oldItem: EmojiEntity, newItem: EmojiEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EmojiEntity, newItem: EmojiEntity): Boolean {
            return oldItem == newItem
        }
    }
    inner class EmojiViewHolder(private val binding: ItemEmojiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init{
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                var item = getItem(position)
                if (isEditMode) {
                    toggleSelection(item.id)
                } else {
                    onItemClick(item)
                }
            }

            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener false
                if (!isEditMode) {
                    onLongClick()
                    toggleSelection(getItem(position).id)
                    true
                } else {
                    false
                }
            }
        }

        private fun toggleSelection(id : Long){
            if(selectedItems.contains(id)) {
                selectedItems.remove(id)
            } else {
                selectedItems.add(id)
            }
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                notifyItemChanged(bindingAdapterPosition)
            }
        }
        fun bind(item:EmojiEntity) {
            val file = File(item.filePath)

            Glide.with(binding.imageEmoji.context)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(binding.imageEmoji)

            if (isEditMode) {
                binding.checkboxSelect.visibility = View.VISIBLE
                val isSelected = selectedItems.contains(item.id)
                binding.checkboxSelect.isChecked = isSelected
                binding.viewOverlay.visibility = if (isSelected) View.VISIBLE else View.GONE
            } else {
                binding.checkboxSelect.visibility = View.GONE
                binding.viewOverlay.visibility = View.GONE
            }
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