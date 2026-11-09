package com.example.searchapp.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val firstPublishYear: Int?,
    val editionCount: Int
)