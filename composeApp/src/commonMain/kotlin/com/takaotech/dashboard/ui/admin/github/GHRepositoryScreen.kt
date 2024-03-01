package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.takaotech.dashboard.model.MainCategory


class GHRepositoryScreen : Screen {

	@OptIn(ExperimentalMaterialApi::class)
	@Composable
	override fun Content() {
		val viewModel = getScreenModel<GHRepositoryListViewModel>()
		val uiState by viewModel.uiState.collectAsState()



		Column {
			Row(modifier = Modifier.padding(4.dp)) {
				Box {
					var expanded by remember {
						mutableStateOf(false)
					}

					Chip(onClick = { expanded = true }) {
						Text("Prova")
					}

					DropdownMenu(
						modifier = Modifier.fillMaxWidth(),
						expanded = expanded, onDismissRequest = {
							expanded = false
						}
					) {
						MainCategory.entries.forEach {
							DropdownMenuItem(
								onClick = {
									expanded = false
								}
							) {
								Text(it.name)
							}
						}
					}
				}
			}

			GHRepositoryList(
				modifier = Modifier.fillMaxWidth(),
				list = uiState.ghRepositoryData
			)
		}
	}
}