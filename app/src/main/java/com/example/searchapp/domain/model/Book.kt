package com.example.searchapp.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val firstPublishYear: Int?,
    val editionCount: Int
) {
    val category: BookCategory
        get() = when {
            firstPublishYear == null -> BookCategory.UNKNOWN
            firstPublishYear < 1950 -> BookCategory.CLASSIC
            else -> BookCategory.CONTEMPORARY
        }
}
