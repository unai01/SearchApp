package com.example.searchapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.searchapp.presentation.detail.DetailScreen
import com.example.searchapp.presentation.search.SearchScreen
import com.example.searchapp.presentation.search.SearchViewModel

private object Routes {
    const val SEARCH = "search"
    const val DETAIL = "detail"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val searchViewModel: SearchViewModel = hiltViewModel()
    val selectedBook by searchViewModel.selectedBook.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel = searchViewModel,
                onBookClick = { book ->
                    searchViewModel.selectBook(book)
                    navController.navigate(Routes.DETAIL)
                }
            )
        }

        composable(Routes.DETAIL) {
            DetailScreen(
                book = selectedBook,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
