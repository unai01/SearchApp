package com.example.searchapp.domain.usecase

import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.repository.BookRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchBooksUseCaseTest {

    private lateinit var repository: BookRepository
    private lateinit var useCase: SearchBooksUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchBooksUseCase(repository)
    }

    @Test
    fun `blank query returns empty list without calling repository`() = runTest {
        val result = useCase("   ")

        assertTrue(result.isEmpty())
        coVerify(exactly = 0) {
            repository.searchBooks(any())
        }
    }

    @Test
    fun `query is trimmed before calling repository`() = runTest {
        val books = listOf(
            Book(
                id = "/works/OL123W",
                title = "Kotlin",
                author = "Test Author",
                firstPublishYear = 2020,
                editionCount = 2
            )
        )

        coEvery {
            repository.searchBooks("Kotlin")
        } returns books

        val result = useCase("  Kotlin  ")

        assertEquals(books, result)

        coVerify(exactly = 1) {
            repository.searchBooks("Kotlin")
        }
    }

    @Test
    fun `repository results are returned correctly`() = runTest {
        val books = listOf(
            Book(
                id = "1",
                title = "Android Development",
                author = "Author One",
                firstPublishYear = 2021,
                editionCount = 3
            ),
            Book(
                id = "2",
                title = "Advanced Kotlin",
                author = "Author Two",
                firstPublishYear = 2022,
                editionCount = 1
            )
        )

        coEvery {
            repository.searchBooks("Android")
        } returns books

        val result = useCase("Android")

        assertEquals(2, result.size)
        assertEquals(books, result)
    }
}