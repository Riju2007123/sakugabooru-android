package com.sakuga.app.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sakuga.app.R
import com.sakuga.app.databinding.ItemPostBinding
import com.sakuga.app.domain.model.Post

class StaticPostAdapter(
    private val onPostClick: (Post) -> Unit,
    private val onFavoriteClick: (Post) -> Unit
) : ListAdapter<Post, StaticPostAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ItemPostBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(post: Post) {
            b.root.setOnClickListener { onPostClick(post) }
            b.btnFavorite.setOnClickListener { onFavoriteClick(post) }
            b.badgeVideo.visibility = if (post.isVideo) android.view.View.VISIBLE else android.view.View.GONE
            b.badgeGif.visibility   = if (post.isGif)   android.view.View.VISIBLE else android.view.View.GONE
            b.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            b.imgThumbnail.load(post.previewUrl.ifEmpty { post.sampleUrl }) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(o: Post, n: Post) = o.id == n.id
            override fun areContentsTheSame(o: Post, n: Post) = o == n
        }
    }
}
