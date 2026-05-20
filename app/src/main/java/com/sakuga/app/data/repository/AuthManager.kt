package com.sakuga.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth")

data class AuthState(
    val login: String = "",
    val passwordHash: String = "",
    val isLoggedIn: Boolean = false
)

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_LOGIN = stringPreferencesKey("login")
        private val KEY_HASH  = stringPreferencesKey("password_hash")

        // Moebooru salt: "choujin-suki--{password}--"
        private const val SALT_PREFIX = "choujin-suki--"
        private const val SALT_SUFFIX = "--"

        fun hashPassword(password: String): String {
            val salted = "$SALT_PREFIX$password$SALT_SUFFIX"
            val digest = MessageDigest.getInstance("SHA-1")
            val bytes = digest.digest(salted.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    val authState: Flow<AuthState> = context.authDataStore.data.map { prefs ->
        val login = prefs[KEY_LOGIN] ?: ""
        val hash  = prefs[KEY_HASH]  ?: ""
        AuthState(login, hash, login.isNotEmpty() && hash.isNotEmpty())
    }

    suspend fun save(login: String, passwordHash: String) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_LOGIN] = login
            prefs[KEY_HASH]  = passwordHash
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { it.clear() }
    }
}
