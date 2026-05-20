package com.sakuga.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakuga.app.data.repository.Result
import com.sakuga.app.data.repository.SakugaRepository
import com.sakuga.app.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SakugaRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _suggestions = MutableStateFlow<List<Tag>>(emptyList())
    val suggestions: StateFlow<List<Tag>> = _suggestions.asStateFlow()

    init {
        _query
            .debounce(300)
            .filter { it.length >= 2 }
            .distinctUntilChanged()
            .onEach { q ->
                when (val result = repository.searchTags(q)) {
                    is Result.Success -> _suggestions.value = result.data
                    is Result.Error   -> _suggestions.value = emptyList()
                }
            }
            .launchIn(viewModelScope)
    }

    fun setQuery(q: String) { _query.value = q }
    fun clearSuggestions() { _suggestions.value = emptyList() }
}
