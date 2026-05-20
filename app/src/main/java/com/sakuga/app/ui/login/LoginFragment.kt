package com.sakuga.app.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sakuga.app.R
import com.sakuga.app.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _b: FragmentLoginBinding? = null
    private val b get() = _b!!
    private val vm: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentLoginBinding.bind(view)

        b.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        b.btnLogin.setOnClickListener {
            vm.login(
                b.etUsername.text.toString(),
                b.etPassword.text.toString()
            )
        }

        b.btnLogout.setOnClickListener { vm.logout() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.state.collect { state ->
                        b.progressBar.isVisible = state is LoginState.Loading
                        b.tvError.isVisible     = state is LoginState.Error
                        if (state is LoginState.Error) b.tvError.text = state.message
                        if (state is LoginState.Success) findNavController().navigateUp()
                    }
                }
                launch {
                    vm.authState.collect { auth ->
                        val loggedIn = auth?.isLoggedIn == true
                        b.layoutLogin.isVisible  = !loggedIn
                        b.layoutLogout.isVisible = loggedIn
                        if (loggedIn) b.tvLoggedInAs.text = "Logged in as: ${auth!!.login}"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
