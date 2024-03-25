package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.ui.admin.github.GHRepositoryListViewModel
import com.takaotech.dashboard.ui.utils.toColor
import kotlinx.coroutines.flow.receiveAsFlow

data class TagSelectionList(val repositoryId: Long) : Screen {

	@OptIn(ExperimentalMaterialApi::class)
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val viewModel = navigator.getNavigatorScreenModel<GHRepositoryListViewModel>()
		val tagSelectionListViewModel = getScreenModel<TagSelectionListViewModel>()

		val uiState by tagSelectionListViewModel.uiState.collectAsState()

		LaunchedEffect(Unit) {
			val tags = viewModel.getAssignedTags(repositoryId)
			tagSelectionListViewModel.init(tags)
		}

		LaunchedEffect(Unit) {
			tagSelectionListViewModel.refreshRepositoryChannel.receiveAsFlow().collect {
				if (it != null) {
					viewModel.refreshGHRepository(repositoryId)
					navigator.pop()
				}
			}
		}

		Scaffold(modifier = Modifier.fillMaxSize(),
			bottomBar = {
				Button(
					modifier = Modifier.fillMaxWidth(),
					onClick = {
						tagSelectionListViewModel.updateTags(repositoryId)
					}
				) {
					Text("Save")
				}
			}
		) {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(it),
				contentPadding = PaddingValues(16.dp)
			) {
				items(uiState.tagList) {
					val chipBackgroundColor =
						it.color?.let { color -> ChipDefaults.chipColors(backgroundColor = color.toColor()) }
							?: ChipDefaults.chipColors()
					Chip(
						onClick = {
							tagSelectionListViewModel.changeTagSelection(it.id, !it.selected)
						},
						colors = chipBackgroundColor,
						leadingIcon = {
							if (it.selected) {
								//TODO Add Content Description
								Icon(Icons.Filled.Check, "ch")
							}
						}
					) {
						//TODO Adapt text color from Chip Background color
						val isDark = it.color?.toColor()?.luminance()?.let { luminance ->
							luminance < 0.5
						} ?: false

						Text(
							color = if (isDark) {
								Color.White
							} else {
								Color.Black
							},
							text = it.name
						)
					}
				}
			}

		}
	}
}