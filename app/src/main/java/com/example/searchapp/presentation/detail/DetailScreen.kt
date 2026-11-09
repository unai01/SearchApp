package com.example.searchapp.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.searchapp.R
import com.example.searchapp.domain.model.Book
import com.example.searchapp.domain.model.BookCategory

@Composable
fun DetailScreen(
    book: Book?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Button(onClick = onBack) {
            Text(stringResource(R.string.detail_back))
        }

        if (book == null) {
            Text(
                text = stringResource(R.string.detail_not_found),
                style = MaterialTheme.typography.bodyLarge
            )
            return@Column
        }

        Text(
            text = stringResource(R.string.detail_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = book.title.ifBlank {
                stringResource(R.string.book_unknown_title)
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.detail_section),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DetailField(
                    label = stringResource(R.string.detail_author),
                    value = book.author.ifBlank {
                        stringResource(R.string.book_unknown_author)
                    }
                )

                DetailField(
                    label = stringResource(
                        R.string.detail_first_published
                    ),
                    value = book.firstPublishYear
                        ?.toString()
                        ?: stringResource(R.string.detail_unknown)
                )

                DetailField(
                    label = stringResource(R.string.detail_editions),
                    value = book.editionCount.toString()
                )

                DetailField(
                    label = stringResource(R.string.detail_category),
                    value = when (book.category) {
                        BookCategory.CLASSIC ->
                            stringResource(R.string.book_category_classic)

                        BookCategory.CONTEMPORARY ->
                            stringResource(
                                R.string.book_category_contemporary
                            )

                        BookCategory.UNKNOWN ->
                            stringResource(R.string.book_category_unknown)
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}