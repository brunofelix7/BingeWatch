package me.brunofelix.bingewatch.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.databinding.LoadStateFooterViewItemBinding

class ItemLoadStateViewHolder(
    private val binding: LoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.also {
            it.setOnClickListener { retry.invoke() }
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState !is LoadState.Loading
        binding.errorMsg.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ItemLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.load_state_footer_view_item, parent, false)
            val binding = LoadStateFooterViewItemBinding.bind(view)
            return ItemLoadStateViewHolder(binding, retry)
        }
    }
}