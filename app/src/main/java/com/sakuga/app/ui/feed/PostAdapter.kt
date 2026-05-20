package com.sakuga.app.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sakuga.app.R
import com.sakuga.app.databinding.ItemPostBinding
import com.sakuga.app.domain.model.Post

class PostAdapter(
    private val onPostClick: (Post) -> Unit,
    private val onFavoriteClick: (Post) -> Unit
) : PagingDataAdapter<Post, PostAdapter.PostViewHolder>(POST_DIFF) {

    var favoriteIds: Set<Int> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, it.id in favoriteIds) }
    }

    inner class PostViewHolder(private val b: ItemPostBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(post: Post, isFav: Boolean) {
            b.root.setOnClickListener { onPostClick(post) }
            b.btnFavorite.setOnClickListener { onFavoriteClick(post) }

            // video badge
            b.badgeVideo.isVisible = post.isVideo
            b.badgeGif.isVisible = post.isGif

            // favorite icon
            b.btnFavorite.setImageResource(
                if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
            )

            // thumbnail — prefer sample > preview
            val thumbUrl = post.previewUrl.ifEmpty { post.sampleUrl }
            b.imgThumbnail.load(thumbUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
                error(R.drawable.bg_placeholder)
            }
        }
    }

    companion object {
        val POST_DIFF = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(o: Post, n: Post) = o.id == n.id
            override fun areContentsTheSame(o: Post, n: Post) = o == n
        }
    }
}
