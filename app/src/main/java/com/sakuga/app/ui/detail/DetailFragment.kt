package com.sakuga.app.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.chip.Chip
import com.sakuga.app.R
import com.sakuga.app.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _b: FragmentDetailBinding? = null
    private val b get() = _b!!
    private val vm: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private var player: ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentDetailBinding.bind(view)

        val post = args.post
        vm.init(post)

        b.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        // metadata
        b.tvAuthor.text = post.author
        b.tvScore.text  = "Score: ${post.score}"
        b.tvTags.text   = post.tags

        // tags as chips so you can tap them to search
        post.tags.split(" ").filter { it.isNotEmpty() }.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isClickable = true
                setOnClickListener {
                    // go back to feed with this tag pre-filled
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("search_tag", tag)
                    findNavController().navigateUp()
                }
            }
            b.chipGroupTags.addView(chip)
        }

        b.btnFavorite.setOnClickListener { vm.toggleFavorite(post) }

        if (post.isVideo) {
            setupVideoPlayer(post.fileUrl)
        } else {
            b.playerView.isVisible = false
            b.imgPreview.isVisible = true
            b.imgPreview.load(post.fileUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
        }

        observeViewModel()
    }

    private fun setupVideoPlayer(url: String) {
        b.playerView.isVisible = true
        b.imgPreview.isVisible = false

        player = ExoPlayer.Builder(requireContext()).build().also { exo ->
            b.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.repeatMode = Player.REPEAT_MODE_ONE
            exo.playWhenReady = true
            exo.prepare()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.isFavorited.collect { fav ->
                    b.btnFavorite.setImageResource(
                        if (fav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _b = null
    }
}
