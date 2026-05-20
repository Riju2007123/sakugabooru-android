package com.sakuga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sakuga.app.domain.model.Post

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo val tags: String,
    @ColumnInfo val author: String,
    @ColumnInfo val score: Int,
    @ColumnInfo val fileUrl: String,
    @ColumnInfo val previewUrl: String,
    @ColumnInfo val sampleUrl: String,
    @ColumnInfo val fileExt: String,
    @ColumnInfo val width: Int,
    @ColumnInfo val height: Int,
    @ColumnInfo val rating: String,
    @ColumnInfo val source: String,
    @ColumnInfo val savedAt: Long = System.currentTimeMillis()
)

fun FavoriteEntity.toDomain(): Post = Post(
    id = id,
    tags = tags,
    createdAt = savedAt,
    creatorId = null,
    author = author,
    score = score,
    source = source,
    md5 = "",
    fileSize = 0,
    fileUrl = fileUrl,
    previewUrl = previewUrl,
    sampleUrl = sampleUrl,
    previewWidth = 0,
    previewHeight = 0,
    sampleWidth = width,
    sampleHeight = height,
    width = width,
    height = height,
    fileExt = fileExt,
    rating = rating,
    status = "active",
    isFavorited = true
)

fun Post.toEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    tags = tags,
    author = author,
    score = score,
    fileUrl = fileUrl,
    previewUrl = previewUrl,
    sampleUrl = sampleUrl,
    fileExt = fileExt,
    width = width,
    height = height,
    rating = rating,
    source = source
)
