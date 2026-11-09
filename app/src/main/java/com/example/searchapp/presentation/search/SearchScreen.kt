package com.example.searchapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.searchapp.R
import com.example.searchapp.domain.model.Book

@Composable
fun SearchScreen(
    onBookClick: (Book) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val loadingDescription = stringResource(R.string.search_loading)

    val errorMessage = when (state.error) {
        "ERROR_NETWORK" -> R.string.search_error_network
        "ERROR_TIMEOUT" -> R.string.search_error_timeout
        "ERROR_SERVER" -> R.string.search_error_server
        else -> R.string.search_error
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.search_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            label = {
                Text(stringResource(R.string.search_hint))
            },
            placeholder = {
                Text(stringResource(R.string.search_placeholder))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { viewModel.search() }
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Button(
            onClick = viewModel::search,
            enabled = state.query.isNotBlank() && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.search_button))
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.semantics {
                            contentDescription = loadingDescription
                        }
                    )
                }
            }

            state.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(errorMessage),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        OutlinedButton(
                            onClick = viewModel::search
                        ) {
                            Text(stringResource(R.string.search_retry))
                        }
                    }
                }
            }

            state.books.isNotEmpty() -> {
                Text(
                    text = stringResource(
                        R.string.search_results_count,
                        state.books.size
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.books) { book ->
                        BookResultCard(
                            book = book,
                            onClick = { onBookClick(book) }
                        )
                    }
                }
            }

            state.query.isBlank() -> {
                EmptySearchMessage(
                    title = stringResource(
                        R.string.search_initial_title
                    ),
                    description = stringResource(
                        R.string.search_initial_description
                    )
                )
            }

            !state.hasSearched -> {
                EmptySearchMessage(
                    title = stringResource(
                        R.string.search_ready_title
                    ),
                    description = stringResource(
                        R.string.search_ready_description
                    )
                )
            }

            else -> {
                EmptySearchMessage(
                    title = stringResource(
                        R.string.search_empty_title
                    ),
                    description = stringResource(
                        R.string.search_empty_description
                    )
                )
            }
        }
    }
}

@Composable
private fun BookResultCard(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = book.title.ifBlank {
                    stringResource(R.string.book_unknown_title)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = book.author.ifBlank {
                    stringResource(R.string.book_unknown_author)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                book.firstPublishYear?.let { year ->
                    Text(
                        text = stringResource(
                            R.string.book_year,
                            year
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    text = stringResource(
                        R.string.book_editions,
                        book.editionCount
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun EmptySearchMessage(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}