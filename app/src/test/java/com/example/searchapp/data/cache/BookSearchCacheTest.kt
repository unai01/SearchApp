package com.example.searchapp.data.cache

import com.example.searchapp.domain.model.Book
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BookSearchCacheTest {

    private lateinit var cache: BookSearchCache
    private var fakeTime = 0L

    private val books = listOf(
        Book(
            id = "1",
            title = "Kotlin",
            author = "Test Author",
            firstPublishYear = 2020,
            editionCount = 2
        )
    )

    @Before
    fun setUp() {
        fakeTime = 0L
        cache = BookSearchCache()
        cache.currentTimeMillis = { fakeTime }
    }

    @Test
    fun `cached results are returned before expiration`() {
        cache.put("Kotlin", books)

        fakeTime += 60_000L

        assertEquals(books, cache.get("Kotlin"))
    }

    @Test
    fun `cached results expire after three minutes`() {
        cache.put("Kotlin", books)

        fakeTime += 180_000L

        assertNull(cache.get("Kotlin"))
    }

    @Test
    fun `different queries have independent cache entries`() {
        cache.put("Kotlin", books)

        assertEquals(books, cache.get("Kotlin"))
        assertNull(cache.get("Android"))
    }

    @Test
    fun `queries are normalized before accessing cache`() {
        cache.put(" Kotlin ", books)

        assertEquals(books, cache.get("kotlin"))
        assertEquals(books, cache.get("KOTLIN"))
    }
}