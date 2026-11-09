package com.example.searchapp.presentation.search

import androidx.lifecycle.SavedStateHandle
import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.repository.BookRepository
import com.example.searchapp.domain.usecase.SearchBooksUseCase
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
            SearchBooksUseCase(repository),
            SavedStateHandle()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `new search cancels previous search results`() =
        runTest(dispatcher) {

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

            coEvery {
                repository.searchBooks("Kotlin")
            } coAnswers {
                delay(5_000)
                listOf(kotlinBook)
            }

            coEvery {
                repository.searchBooks("Android")
            } returns listOf(androidBook)

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

    @Test
    fun `selected book is restored from saved state`() {

        val savedStateHandle = SavedStateHandle()

        val book = Book(
            id = "1",
            title = "Kotlin Book",
            author = "Test Author",
            firstPublishYear = 2020,
            editionCount = 3
        )

        val originalViewModel = SearchViewModel(
            SearchBooksUseCase(repository),
            savedStateHandle
        )

        originalViewModel.selectBook(book)

        val restoredViewModel = SearchViewModel(
            SearchBooksUseCase(repository),
            savedStateHandle
        )

        assertEquals(
            book,
            restoredViewModel.selectedBook.value
        )
    }

    @Test
    fun `blank query does not trigger repository search`() =
        runTest(dispatcher) {

            viewModel.onQueryChange("   ")
            viewModel.search()

            advanceUntilIdle()

            io.mockk.coVerify(exactly = 0) {
                repository.searchBooks(any())
            }

            assertEquals(
                false,
                viewModel.uiState.value.isLoading
            )

            assertEquals(
                emptyList<Book>(),
                viewModel.uiState.value.books
            )
        }

    @Test
    fun `repository error updates UI state and stops loading`() =
        runTest(dispatcher) {

            coEvery {
                repository.searchBooks("Kotlin")
            } throws RuntimeException("Network error")

            viewModel.onQueryChange("Kotlin")
            viewModel.search()

            advanceUntilIdle()

            assertEquals(
                false,
                viewModel.uiState.value.isLoading
            )

            assertEquals(
                "ERROR_UNKNOWN",
                viewModel.uiState.value.error
            )

            assertEquals(
                emptyList<Book>(),
                viewModel.uiState.value.books
            )
        }


    @Test
    fun `network error is classified correctly`() =
        runTest(dispatcher) {
            coEvery {
                repository.searchBooks("Kotlin")
            } throws IOException("Connection failed")

            viewModel.onQueryChange("Kotlin")
            viewModel.search()

            advanceUntilIdle()

            assertEquals(
                "ERROR_NETWORK",
                viewModel.uiState.value.error
            )
            assertEquals(
                false,
                viewModel.uiState.value.isLoading
            )
        }

    @Test
    fun `timeout error is classified correctly`() =
        runTest(dispatcher) {
            coEvery {
                repository.searchBooks("Kotlin")
            } throws SocketTimeoutException("Read timed out")

            viewModel.onQueryChange("Kotlin")
            viewModel.search()

            advanceUntilIdle()

            assertEquals(
                "ERROR_TIMEOUT",
                viewModel.uiState.value.error
            )
            assertEquals(
                false,
                viewModel.uiState.value.isLoading
            )
        }

    @Test
    fun `http error is classified correctly`() =
        runTest(dispatcher) {
            coEvery {
                repository.searchBooks("Kotlin")
            } throws HttpException(
                Response.error<Any>(
                    503,
                    okhttp3.ResponseBody.create(
                        null,
                        "Service unavailable"
                    )
                )
            )

            viewModel.onQueryChange("Kotlin")
            viewModel.search()

            advanceUntilIdle()

            assertEquals(
                "ERROR_SERVER",
                viewModel.uiState.value.error
            )
            assertEquals(
                false,
                viewModel.uiState.value.isLoading
            )
        }

}
