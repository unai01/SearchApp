package com.example.searchapp.data.remote

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("docs")
    val books: List<BookDto> = emptyList()
)

data class BookDto(
    @SerializedName("key")
    val key: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("author_name")
    val authors: List<String>?,

    @SerializedName("first_publish_year")
    val firstPublishYear: Int?,

    @SerializedName("edition_count")
    val editionCount: Int?
)