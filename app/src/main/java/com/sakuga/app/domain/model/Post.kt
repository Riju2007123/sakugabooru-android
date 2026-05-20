package com.sakuga.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val tags: String,
    val createdAt: Long,
    val creatorId: Int?,
    val author: String,
    val score: Int,
    val source: String,
    val md5: String,
    val fileSize: Long,
    val fileUrl: String,       // full mp4/webm/jpg url
    val previewUrl: String,    // thumbnail
    val sampleUrl: String,     // medium preview
    val previewWidth: Int,
    val previewHeight: Int,
    val sampleWidth: Int,
    val sampleHeight: Int,
    val width: Int,
    val height: Int,
    val fileExt: String,       // mp4, webm, jpg, png, gif
    val rating: String,        // s/q/e
    val status: String,
    val isFavorited: Boolean = false
) : Parcelable {
    val isVideo: Boolean get() = fileExt == "mp4" || fileExt == "webm"
    val isGif: Boolean get() = fileExt == "gif"
}
