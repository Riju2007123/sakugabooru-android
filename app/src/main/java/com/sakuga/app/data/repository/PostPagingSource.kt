package com.sakuga.app.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sakuga.app.data.api.SakugaApi
import com.sakuga.app.data.api.toDomain
import com.sakuga.app.domain.model.Post

class PostPagingSource(
    private val api: SakugaApi,
    private val tags: String,
    private val login: String?,
    private val passwordHash: String?,
    private val favoritedIds: Set<Int>
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page = params.key ?: 1
        return try {
            val response = api.getPosts(
                limit = params.loadSize.coerceAtMost(100),
                page = page,
                tags = tags,
                login = login,
                passwordHash = passwordHash
            )
            if (!response.isSuccessful) {
                return LoadResult.Error(Exception("HTTP ${response.code()}"))
            }
            val posts = response.body()
                ?.filter { it.fileUrl != null }   // skip deleted/DMCA'd posts
                ?.map { it.toDomain(isFavorited = it.id in favoritedIds) }
                ?: emptyList()

            LoadResult.Page(
                data = posts,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (posts.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
