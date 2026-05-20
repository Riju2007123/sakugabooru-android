package com.sakuga.app.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakuga.app.data.repository.SakugaRepository
import com.sakuga.app.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: SakugaRepository
) : ViewModel() {

    val favorites: StateFlow<List<Post>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun removeFavorite(post: Post) {
        viewModelScope.launch { repository.toggleFavorite(post) }
    }
}
