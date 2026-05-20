package com.sakuga.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakuga.app.data.repository.Result
import com.sakuga.app.data.repository.SakugaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: SakugaRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    val authState = repository.authState
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error("Username and password required")
            return
        }
        viewModelScope.launch {
            _state.value = LoginState.Loading
            _state.value = when (val result = repository.login(username, password)) {
                is Result.Success -> LoginState.Success
                is Result.Error   -> LoginState.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.value = LoginState.Idle
        }
    }
}
