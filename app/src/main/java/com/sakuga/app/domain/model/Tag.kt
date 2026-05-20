package com.sakuga.app.domain.model

data class Tag(
    val id: Int,
    val name: String,
    val count: Int,
    val type: Int  // 0=general, 1=artist, 3=copyright, 4=character
) {
    val typeName: String get() = when (type) {
        1 -> "artist"
        3 -> "copyright"
        4 -> "character"
        else -> "general"
    }
}
