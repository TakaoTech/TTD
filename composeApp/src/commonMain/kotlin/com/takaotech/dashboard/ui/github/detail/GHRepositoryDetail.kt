package com.takaotech.dashboard.ui.github.detail

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import com.takaotech.dashboard.ui.utils.toColor
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data class GHRepositoryDetail(@SerialName("repositoryId") val id: Long)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GHRepositoryDetailPage(
    viewModel: GHRepositoryDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalTTDUriHandler.current

    val logger = koinInject<Logger>()

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
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
                                },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, "")
                            }
                        },
                    )
                }

                else -> Unit
            }
        },
    ) {
        when (val repositoryUiState = uiState.repositoryUiState) {
            GHRepositoryDetailUi.GHRepositoryDetailUiState.Error -> {
            }

            GHRepositoryDetailUi.GHRepositoryDetailUiState.Loading -> {
            }

            is GHRepositoryDetailUi.GHRepositoryDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                ) {
                    AsyncImage(
                        modifier = Modifier.size(32.dp),
                        model = repositoryUiState.repository.user.avatarUrl,
                        contentDescription = null,
                    )

                    var chartData by remember(repositoryUiState.repository.languages) {
                        mutableStateOf(
                            repositoryUiState.repository.languages.map {
                                logger.i { "Build ${it.name}" }
                                Pie(
                                    label = it.name,
                                    data = it.lines.toDouble(),
                                    color = it.colorCode?.toColor()!!,
                                )
                            },
                        )
                    }

                    PieChart(
                        modifier = Modifier.size(200.dp),
                        data = chartData,
                        onPieClick = {
                            println("${it.label} Clicked")
                            val pieIndex = chartData.indexOf(it)
                            chartData =
                                chartData.mapIndexed { mapIndex, pie ->
                                    if (pieIndex == mapIndex) {
                                        pie.copy(selected = !pie.selected)
                                    } else {
                                        pie.copy(selected = false)
                                    }
                                }
                        },
                        scaleAnimEnterSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                        colorAnimEnterSpec = tween(300),
                        colorAnimExitSpec = tween(300),
                        scaleAnimExitSpec = tween(300),
                        spaceDegreeAnimExitSpec = tween(300),
                    )
                }
            }
        }
    }
}