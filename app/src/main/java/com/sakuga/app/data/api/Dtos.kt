package com.sakuga.app.data.api

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tags") val tags: String,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("creator_id") val creatorId: Int?,
    @SerializedName("author") val author: String,
    @SerializedName("score") val score: Int,
    @SerializedName("source") val source: String?,
    @SerializedName("md5") val md5: String?,
    @SerializedName("file_size") val fileSize: Long,
    @SerializedName("file_url") val fileUrl: String?,
    @SerializedName("preview_url") val previewUrl: String?,
    @SerializedName("sample_url") val sampleUrl: String?,
    @SerializedName("preview_width") val previewWidth: Int,
    @SerializedName("preview_height") val previewHeight: Int,
    @SerializedName("sample_width") val sampleWidth: Int,
    @SerializedName("sample_height") val sampleHeight: Int,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("file_ext") val fileExt: String?,
    @SerializedName("rating") val rating: String,
    @SerializedName("status") val status: String
)

data class TagDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("count") val count: Int,
    @SerializedName("type") val type: Int,
    @SerializedName("ambiguous") val ambiguous: Boolean
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("reason") val reason: String?,
    // user info returned on successful profile fetch
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("api_key") val apiKey: String?
)
