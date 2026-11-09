
package com.example.searchapp.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.searchapp.domain.model.Book

@Composable
fun SearchScreen(
    onBookClick: (Book) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Buscar libros") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::search,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(error)
        }

        LazyColumn {
            items(state.books) { book ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookClick(book) }
                        .padding(vertical = 12.dp)
                ) {
                    Text(text = book.title)
                    Text(text = book.author)
                }

                HorizontalDivider()
            }
        }
    }
}
