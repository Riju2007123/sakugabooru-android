package com.sakuga.app.data.api

import com.sakuga.app.domain.model.Post
import com.sakuga.app.domain.model.Tag

fun PostDto.toDomain(isFavorited: Boolean = false): Post {
    // derive file extension from url if missing
    val ext = fileExt
        ?: fileUrl?.substringAfterLast('.')?.lowercase()?.take(4)
        ?: "jpg"
    return Post(
        id = id,
        tags = tags,
        createdAt = createdAt,
        creatorId = creatorId,
        author = author,
        score = score,
        source = source ?: "",
        md5 = md5 ?: "",
        fileSize = fileSize,
        fileUrl = fileUrl ?: "",
        previewUrl = previewUrl ?: "",
        sampleUrl = sampleUrl ?: previewUrl ?: "",
        previewWidth = previewWidth,
        previewHeight = previewHeight,
        sampleWidth = sampleWidth,
        sampleHeight = sampleHeight,
        width = width,
        height = height,
        fileExt = ext,
        rating = rating,
        status = status,
        isFavorited = isFavorited
    )
}

fun TagDto.toDomain(): Tag = Tag(
    id = id,
    name = name,
    count = count,
    type = type
)
