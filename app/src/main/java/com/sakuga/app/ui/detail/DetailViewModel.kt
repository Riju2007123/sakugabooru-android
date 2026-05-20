package com.sakuga.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakuga.app.data.repository.SakugaRepository
import com.sakuga.app.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: SakugaRepository
) : ViewModel() {

    private val _isFavorited = MutableStateFlow(false)
    val isFavorited: StateFlow<Boolean> = _isFavorited.asStateFlow()

    fun init(post: Post) {
        viewModelScope.launch {
            _isFavorited.value = repository.isFavorited(post.id)
        }
    }

    fun toggleFavorite(post: Post) {
        viewModelScope.launch {
            repository.toggleFavorite(post)
            _isFavorited.value = repository.isFavorited(post.id)
        }
    }
}
