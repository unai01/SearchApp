
package com.example.searchapp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BookCategoryTest {

    private fun bookWithYear(year: Int?) = Book(
        id = "test-id",
        title = "Test Book",
        author = "Test Author",
        firstPublishYear = year,
        editionCount = 1
    )

    @Test
    fun `book published before 1950 is classic`() {
        assertEquals(
            BookCategory.CLASSIC,
            bookWithYear(1949).category
        )
    }

    @Test
    fun `book published in 1950 is contemporary`() {
        assertEquals(
            BookCategory.CONTEMPORARY,
            bookWithYear(1950).category
        )
    }

    @Test
    fun `book published after 1950 is contemporary`() {
        assertEquals(
            BookCategory.CONTEMPORARY,
            bookWithYear(2020).category
        )
    }

    @Test
    fun `book without publication year is unknown`() {
        assertEquals(
            BookCategory.UNKNOWN,
            bookWithYear(null).category
        )
    }
}
