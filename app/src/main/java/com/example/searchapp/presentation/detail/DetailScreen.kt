package com.example.searchapp.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.searchapp.domain.model.Book

@Composable
fun DetailScreen(
    book: Book?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Volver")
        }

        if (book == null) {
            Text("No se ha encontrado el libro seleccionado.")
            return@Column
        }

        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Autor: ${book.author}")

        Text(
            "Primera publicación: " +
                    (book.firstPublishYear?.toString() ?: "Desconocida")
        )

        Text("Número de ediciones: ${book.editionCount}")
    }
}