package com.sakuga.app.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sakuga.app.R
import com.sakuga.app.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _b: FragmentFavoritesBinding? = null
    private val b get() = _b!!
    private val vm: FavoritesViewModel by viewModels()
    private lateinit var adapter: StaticPostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentFavoritesBinding.bind(view)

        adapter = StaticPostAdapter(
            onPostClick = { post ->
                val action = FavoritesFragmentDirections.actionFavoritesToDetail(post)
                findNavController().navigate(action)
            },
            onFavoriteClick = { post ->
                vm.removeFavorite(post)
            }
        )

        b.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = this@FavoritesFragment.adapter
            setHasFixedSize(false)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.favorites.collect { posts ->
                    b.tvEmpty.isVisible = posts.isEmpty()
                    adapter.submitList(posts)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
