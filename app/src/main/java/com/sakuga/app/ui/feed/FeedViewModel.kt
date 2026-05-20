package com.sakuga.app.ui.feed

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sakuga.app.data.repository.SakugaRepository
import com.sakuga.app.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: SakugaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Debounce so we don't hammer the API on every keystroke
    val posts: Flow<PagingData<Post>> = _searchQuery
        .debounce(400)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.getPostStreamAuthenticated(tags = query)
        }
        .cachedIn(viewModelScope)

    val authState = repository.authState
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val favoriteIds: StateFlow<Set<Int>> = repository.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    fun setQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(post: Post) {
        viewModelScope.launch {
            repository.toggleFavorite(post)
        }
    }
}
