package com.example.searchapp.data.mapper

import com.example.searchapp.data.remote.BookDto
import com.example.searchapp.domain.model.Book

fun BookDto.toDomain(): Book {
    return Book(
        id = key.orEmpty(),
        title = title.orEmpty(),
        author = authors
            ?.joinToString(", ")
            .orEmpty(),
        firstPublishYear = firstPublishYear,
        editionCount = editionCount ?: 0
    )
}