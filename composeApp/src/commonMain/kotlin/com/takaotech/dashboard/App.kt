package com.takaotech.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.takaotech.dashboard.ui.admin.AdminSection
import com.takaotech.dashboard.ui.credits.CreditPage
import com.takaotech.dashboard.ui.github.detail.GHRepositoryDetail
import com.takaotech.dashboard.ui.github.detail.GHRepositoryDetailPage
import com.takaotech.dashboard.ui.github.detail.GHRepositoryDetailViewModel
import com.takaotech.dashboard.ui.github.list.GHHomepageListPage
import com.takaotech.dashboard.ui.github.list.GHHomepageListPageViewModel
import com.takaotech.dashboard.ui.github.list.GHListDestination
import com.takaotech.dashboard.ui.github.list.GHListPage
import com.takaotech.dashboard.ui.github.list.GHTagsList
import com.takaotech.dashboard.ui.github.list.GHTagsPage
import com.takaotech.dashboard.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object Home

@Composable
fun App() {
    KoinContext {
        AppTheme {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Home
            ) {
                composable<Home> {
                    HomePage(
                        onTagClicked = { tagId ->
                            navController.navigate(GHListPage(tagId))
                        },
                        onMoreTagClicked = {
                            navController.navigate(GHTagsList)
                        },
                        onRepositoryClicked = {
                            navController.navigate(GHRepositoryDetail(it))
                        },
                        onMoreRepositoriesClicked = {
                            navController.navigate(GHListDestination)
                        },
                        onCreditClicked = {
                            navController.navigate("credits")
                        },
                        onAdminClick = {
                            navController.navigate("admin")
                        }
                    )
                }

                navigation<GHListDestination>(startDestination = GHListPage()) {
                    composable<GHListPage> {
                        val backStackEntry = remember { navController.getBackStackEntry(GHListDestination) }
                        val viewModel = koinViewModel<GHHomepageListPageViewModel>(viewModelStoreOwner = backStackEntry)
                        GHHomepageListPage(
                            viewModel,
                            onRepositoryClicked = {
                                navController.navigate(GHRepositoryDetail(it))
                            }
                        )
                    }

                    composable<GHRepositoryDetail> {
                        val viewModel = koinViewModel<GHRepositoryDetailViewModel>()

                        GHRepositoryDetailPage(
                            viewModel = viewModel
                        )
                    }

                    composable<GHTagsList> {
                        GHTagsPage()
                    }
                }

                composable("admin") {
                    AdminSection()
                }

                composable("credits") {
                    CreditPage(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
