package com.example.searchapp.presentation.search

import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.repository.BookRepository
import com.example.searchapp.domain.usecase.SearchBooksUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: BookRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        repository = mockk()
        viewModel = SearchViewModel(
            SearchBooksUseCase(repository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `new search cancels previous search results`() = runTest(dispatcher) {
        val kotlinBook = Book(
            id = "1",
            title = "Kotlin Book",
            author = "Author One",
            firstPublishYear = 2020,
            editionCount = 1
        )

        val androidBook = Book(
            id = "2",
            title = "Android Book",
            author = "Author Two",
            firstPublishYear = 2022,
            editionCount = 2
        )

        coEvery { repository.searchBooks("Kotlin") } coAnswers {
            delay(5_000)
            listOf(kotlinBook)
        }

        coEvery { repository.searchBooks("Android") } returns
                listOf(androidBook)

        viewModel.onQueryChange("Kotlin")
        viewModel.search()

        // Permitimos que comience la primera búsqueda.
        runCurrent()

        viewModel.onQueryChange("Android")
        viewModel.search()

        advanceUntilIdle()

        assertEquals(
            listOf(androidBook),
            viewModel.uiState.value.books
        )

        assertEquals(
            "Android",
            viewModel.uiState.value.query
        )
    }
}