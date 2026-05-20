package com.sakuga.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sakuga.app.databinding.ItemLoadStateBinding

class FooterLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<FooterLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = ItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class ViewHolder(
        private val b: ItemLoadStateBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(b.root) {
        init { b.btnRetry.setOnClickListener { retry() } }

        fun bind(state: LoadState) {
            b.progressBar.isVisible = state is LoadState.Loading
            b.btnRetry.isVisible   = state is LoadState.Error
            b.tvError.isVisible    = state is LoadState.Error
            if (state is LoadState.Error) {
                b.tvError.text = state.error.message ?: "Unknown error"
            }
        }
    }
}
