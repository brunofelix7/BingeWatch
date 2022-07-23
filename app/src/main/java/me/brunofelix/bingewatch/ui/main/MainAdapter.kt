package me.brunofelix.bingewatch.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.brunofelix.bingewatch.data.Series
import me.brunofelix.bingewatch.databinding.ItemSeriesBinding
import me.brunofelix.bingewatch.util.convertFromTimestamp

class MainAdapter : PagingDataAdapter<Series, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    var listener: MainClickListener? = null
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val root = ItemSeriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(root, listener, context)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Series>() {
            override fun areItemsTheSame(oldItem: Series, newItem: Series) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Series, newItem: Series) = oldItem == newItem
        }
    }

    /**
     * My ViewHolder
     */
    inner class MainViewHolder constructor(
        private val binding: ItemSeriesBinding,
        private val listener: MainClickListener?,
        private val context: Context
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(series: Series) {
            binding.tvName.text = series.name
            binding.tvStartDate.text = convertFromTimestamp(series.startDate)

            binding.layoutRoot.setOnClickListener {
                listener?.onItemClick(series)
            }
        }
    }
}