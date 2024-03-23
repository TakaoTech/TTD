package com.takaotech.dashboard.ui.github.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import coil3.compose.AsyncImage
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import org.koin.core.parameter.parametersOf

data class GHRepositoryDetail(
	private val repositoryId: Long
) : Screen {

	@Composable
	override fun Content() {
		val uriHandler = LocalTTDUriHandler.current

		val viewModel = getScreenModel<GHRepositoryDetailViewModel>(
			parameters = { parametersOf(repositoryId) }
		)

		val uiState by viewModel.uiState.collectAsState()

		when (val repositoryUiState = uiState.repositoryUiState) {
			GHRepositoryDetailUi.GHRepositoryDetailUiState.Error -> {

			}

			GHRepositoryDetailUi.GHRepositoryDetailUiState.Loading -> {
				LinearProgressIndicator()
			}

			is GHRepositoryDetailUi.GHRepositoryDetailUiState.Success -> {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					topBar = {
						TopAppBar(
							title = {
								Text(repositoryUiState.repository.fullName)
							},
							actions = {
								IconButton(
									onClick = {
										uriHandler.openUrl(repositoryUiState.repository.url)
									}
								) {
									Icon(Icons.AutoMirrored.Filled.OpenInNew, "")
								}
							}
						)
					}) {
					Column(
						modifier = Modifier.fillMaxSize()
							.padding(it)
							.verticalScroll(rememberScrollState())
					) {
						AsyncImage(
							modifier = Modifier.size(32.dp),
							model = repositoryUiState.repository.user.avatarUrl,
							contentDescription = null,
						)
					}
				}
			}
		}


	}
}