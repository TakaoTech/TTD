package com.takaotech.dashboard.ui.github

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object HomePageTab : Tab {

	@Composable
	override fun Content() {
		//https://proandroiddev.com/collapsing-toolbar-in-jetpack-compose-lazycolumn-3-approaches-702684d61843
		val viewModel = getScreenModel<HomePageViewModel>()
		val uiState by viewModel.uiState.collectAsState()

		HomePageScreen(
			tags = uiState.tags,
			repositories = uiState.repositoryList,
			onTagClicked = {

			},
			onMoreTagClicked = {

			},
			onMoreRepositoriesClicked = {

			}
		)
	}

	override val options: TabOptions
		@Composable
		get() {
			val title = "Home"
			val icon = rememberVectorPainter(Icons.Filled.Home)

			return remember {
				TabOptions(
					index = 0u,
					title = title,
					icon = icon
				)
			}
		}

}