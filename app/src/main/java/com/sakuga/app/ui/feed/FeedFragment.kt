package com.sakuga.app.ui.feed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.sakuga.app.R
import com.sakuga.app.databinding.FragmentFeedBinding
import com.sakuga.app.ui.common.FooterLoadStateAdapter
import com.sakuga.app.ui.search.SearchViewModel
import com.sakuga.app.ui.search.TagAutocompletePopup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private var _b: FragmentFeedBinding? = null
    private val b get() = _b!!
    private val vm: FeedViewModel by viewModels()
    private val searchVm: SearchViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    private lateinit var tagPopup: TagAutocompletePopup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentFeedBinding.bind(view)

        setupRecycler()
        setupSearch()
        setupSwipeRefresh()
        observeViewModel()

        // Handle tag-tap from DetailFragment (back-nav with savedState)
        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("search_tag")
            ?.observe(viewLifecycleOwner) { tag ->
                if (tag != null) {
                    b.etSearch.setText(tag)
                    b.etSearch.setSelection(tag.length)
                }
            }
    }

    private fun setupRecycler() {
        adapter = PostAdapter(
            onPostClick = { post ->
                val action = FeedFragmentDirections.actionFeedToDetail(post)
                findNavController().navigate(action)
            },
            onFavoriteClick = { post -> vm.toggleFavorite(post) }
        )

        val footerAdapter = FooterLoadStateAdapter { adapter.retry() }

        b.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                        if (position == adapter.itemCount) 2 else 1
                }
            }
            this.adapter = this@FeedFragment.adapter.withLoadStateFooter(footerAdapter)
            setHasFixedSize(true)
        }

        adapter.addLoadStateListener { loadStates ->
            val refresh = loadStates.refresh
            b.progressBarInitial.isVisible = refresh is LoadState.Loading && adapter.itemCount == 0
            b.swipeRefresh.isRefreshing    = refresh is LoadState.Loading && adapter.itemCount > 0
            b.tvEmpty.isVisible            = refresh is LoadState.NotLoading && adapter.itemCount == 0
            b.tvError.isVisible            = refresh is LoadState.Error
            if (refresh is LoadState.Error) {
                b.tvError.text = refresh.error.message ?: "Failed to load"
            }
        }
    }

    private fun setupSearch() {
        tagPopup = TagAutocompletePopup(requireContext(), b.etSearch) { tag ->
            // replace the last partial token with the selected tag
            val current = b.etSearch.text.toString()
            val tokens  = current.trimEnd().split(" ").toMutableList()
            if (tokens.isNotEmpty()) tokens[tokens.lastIndex] = tag.name
            val newText = tokens.joinToString(" ") + " "
            b.etSearch.setText(newText)
            b.etSearch.setSelection(newText.length)
            searchVm.clearSuggestions()
        }

        // Connect popup's fetch lambda to SearchViewModel
        tagPopup.fetchTags = { query ->
            searchVm.setQuery(query)
            // suggestions come via flow — popup reads them directly via callback below
            emptyList() // return handled via suggestions flow observer
        }

        b.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim()
            vm.setQuery(query)
            val lastToken = query.split(" ").lastOrNull { it.isNotEmpty() } ?: ""
            if (lastToken.length >= 2) {
                searchVm.setQuery(lastToken)
            } else {
                searchVm.clearSuggestions()
                tagPopup.dismiss()
            }
        }

        b.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_feed_to_login)
        }
    }

    private fun setupSwipeRefresh() {
        b.swipeRefresh.setOnRefreshListener { adapter.refresh() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    vm.posts.collectLatest { pagingData ->
                        adapter.submitData(pagingData)
                    }
                }

                launch {
                    vm.favoriteIds.collect { ids ->
                        adapter.favoriteIds = ids
                    }
                }

                launch {
                    vm.authState.collect { auth ->
                        val loggedIn = auth?.isLoggedIn == true
                        b.btnLogin.text = if (loggedIn) auth!!.login else getString(R.string.login)
                    }
                }

                // Drive the tag popup from SearchViewModel suggestions
                launch {
                    searchVm.suggestions.collect { tags ->
                        if (tags.isNotEmpty()) {
                            tagPopup.showSuggestions(tags)
                        } else {
                            tagPopup.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tagPopup.dismiss()
        _b = null
    }
}
