package com.example.searchapp.data.repository

import com.example.searchapp.data.cache.BookSearchCache
import com.example.searchapp.data.remote.BookDto
import com.example.searchapp.data.remote.OpenLibraryApi
import com.example.searchapp.data.remote.SearchResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookRepositoryImplTest {

    private lateinit var api: OpenLibraryApi
    private lateinit var cache: BookSearchCache
    private lateinit var repository: BookRepositoryImpl

    private var fakeTime = 0L

    private val response = SearchResponseDto(
        books = listOf(
            BookDto(
                key = "/works/OL123W",
                title = "Kotlin",
                authors = listOf("Test Author"),
                firstPublishYear = 2020,
                editionCount = 2
            )
        )
    )

    @Before
    fun setUp() {
        fakeTime = 0L
        api = mockk()
        cache = BookSearchCache()
        cache.currentTimeMillis = { fakeTime }
        repository = BookRepositoryImpl(api, cache)
    }

    @Test
    fun `first search fetches data from API`() = runTest {
        coEvery { api.searchBooks("Kotlin", any()) } returns response

        val result = repository.searchBooks("Kotlin")

        assertEquals(1, result.size)
        assertEquals("Kotlin", result.first().title)

        coVerify(exactly = 1) {
            api.searchBooks("Kotlin", any())
        }
    }

    @Test
    fun `valid cache avoids second API request`() = runTest {
        coEvery { api.searchBooks("Kotlin", any()) } returns response

        repository.searchBooks("Kotlin")
        repository.searchBooks("Kotlin")

        coVerify(exactly = 1) {
            api.searchBooks("Kotlin", any())
        }
    }

    @Test
    fun `expired cache triggers new API request`() = runTest {
        coEvery { api.searchBooks("Kotlin", any()) } returns response

        repository.searchBooks("Kotlin")

        fakeTime += 180_000L

        repository.searchBooks("Kotlin")

        coVerify(exactly = 2) {
            api.searchBooks("Kotlin", any())
        }
    }
}