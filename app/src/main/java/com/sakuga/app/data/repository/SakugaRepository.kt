package com.sakuga.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sakuga.app.data.api.SakugaApi
import com.sakuga.app.data.api.toDomain
import com.sakuga.app.data.local.dao.FavoriteDao
import com.sakuga.app.data.local.entity.FavoriteEntity
import com.sakuga.app.data.local.entity.toDomain
import com.sakuga.app.data.local.entity.toEntity
import com.sakuga.app.domain.model.Post
import com.sakuga.app.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
}

@Singleton
class SakugaRepository @Inject constructor(
    private val api: SakugaApi,
    private val favoriteDao: FavoriteDao,
    private val authManager: AuthManager
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    // ── Posts ──────────────────────────────────────────────────────────────────

    fun getPostStream(tags: String = ""): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                // snapshot favorite IDs synchronously via blocking first()
                // fine here — called on IO via paging internals
                PostPagingSource(
                    api = api,
                    tags = tags,
                    login = null,       // populated below via authManager
                    passwordHash = null,
                    favoritedIds = emptySet()
                )
            }
        ).flow
    }

    // Version that injects auth + favorite state
    suspend fun getPostStreamAuthenticated(tags: String = ""): Flow<PagingData<Post>> {
        val auth = authManager.authState.first()
        val favIds = favoriteDao.getAllFavoriteIdsList().map { it.toSet() }.first()
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                PostPagingSource(
                    api = api,
                    tags = tags,
                    login = if (auth.isLoggedIn) auth.login else null,
                    passwordHash = if (auth.isLoggedIn) auth.passwordHash else null,
                    favoritedIds = favIds
                )
            }
        ).flow
    }

    // ── Tags autocomplete ──────────────────────────────────────────────────────

    suspend fun searchTags(query: String): Result<List<Tag>> {
        return try {
            val response = api.getTags(namePattern = "$query*")
            if (response.isSuccessful) {
                val tags = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(tags)
            } else {
                Result.Error("Tag search failed", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    // ── Auth ───────────────────────────────────────────────────────────────────

    suspend fun login(username: String, password: String): Result<Unit> {
        val hash = AuthManager.hashPassword(password)
        return try {
            val response = api.getUsers(
                name = username,
                login = username,
                passwordHash = hash
            )
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                authManager.save(username, hash)
                Result.Success(Unit)
            } else {
                Result.Error("Invalid credentials", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun logout() {
        authManager.clear()
    }

    val authState = authManager.authState

    // ── Local Favorites ────────────────────────────────────────────────────────

    fun getFavorites(): Flow<List<Post>> =
        favoriteDao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    fun getFavoriteIds(): Flow<Set<Int>> =
        favoriteDao.getAllFavoriteIdsList().map { it.toSet() }

    suspend fun toggleFavorite(post: Post) {
        if (favoriteDao.isFavorited(post.id)) {
            favoriteDao.delete(post.id)
        } else {
            favoriteDao.insert(post.toEntity())
        }
    }

    suspend fun isFavorited(postId: Int): Boolean =
        favoriteDao.isFavorited(postId)
}
