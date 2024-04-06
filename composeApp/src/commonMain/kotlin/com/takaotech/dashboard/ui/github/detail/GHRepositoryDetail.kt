package com.takaotech.dashboard.ui.github.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import com.takaotech.dashboard.ui.utils.toColor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

data class GHRepositoryDetail(
	private val repositoryId: Long
) : Screen, KoinComponent {

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	override fun Content() {
		val uriHandler = LocalTTDUriHandler.current

		val logger by inject<Logger>()

		val viewModel = getScreenModel<GHRepositoryDetailViewModel>(
			parameters = { parametersOf(repositoryId) }
		)
		val uiState by viewModel.uiState.collectAsState()

		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {

				when (val repositoryUiState = uiState.repositoryUiState) {
					is GHRepositoryDetailUi.GHRepositoryDetailUiState.Success -> {
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
					}

					else -> Unit
				}
			}
		) {
			when (val repositoryUiState = uiState.repositoryUiState) {
				GHRepositoryDetailUi.GHRepositoryDetailUiState.Error -> {

				}

				GHRepositoryDetailUi.GHRepositoryDetailUiState.Loading -> {


				}

				is GHRepositoryDetailUi.GHRepositoryDetailUiState.Success -> {
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

						PieChart(
							modifier = Modifier.size(500.dp),
							pieChartData = repositoryUiState.repository.languages.map {
								logger.i { "Build ${it.name}" }
								PieChartData(
									partName = it.name,
									data = it.lines.toDouble(),
									color = it.colorCode?.toColor()!!,
								)
							},
							outerCircularColor = Color.LightGray,
							ratioLineColor = Color.LightGray,
							legendPosition = LegendPosition.DISAPPEAR
						)
					}
				}
			}

		}


	}
}