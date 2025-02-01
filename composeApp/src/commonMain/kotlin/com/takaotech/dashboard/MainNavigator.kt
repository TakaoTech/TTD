package com.takaotech.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.takaotech.dashboard.ui.LoginPage
import com.takaotech.dashboard.ui.github.HomeTab
import com.takaotech.dashboard.ui.login.SessionManager
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.app_name
import ttd.composeapp.generated.resources.login

@Composable
fun HomePage(
    sessionManager: SessionManager = koinInject(),
    onTagClicked: (tagId: Int) -> Unit,
    onMoreTagClicked: () -> Unit,
    onRepositoryClicked: (repositoryId: Long) -> Unit,
    onMoreRepositoriesClicked: () -> Unit,
    onCreditClicked: () -> Unit,
    onAdminClick: () -> Unit,
) {
    val isAdmin by sessionManager.isAdminFlow().collectAsState(false)

    val topLevelRoute by remember(isAdmin) {
        derivedStateOf {
            buildList {
                add(TopLevelRoute(Res.string.login, "home", Icons.Filled.Home))
                add(TopLevelRoute(Res.string.login, "login", Icons.Filled.Person))
                if (isAdmin) {
                    add(TopLevelRoute(Res.string.app_name, "admin", Icons.Filled.AdminPanelSettings))
                }
            }
        }
    }

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier,
        content = {
            NavHost(navController, startDestination = "home") {
                composable("home") { _ ->
                    Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
                        HomeTab(
                            onTagClicked = onTagClicked,
                            onRepositoryClicked = onRepositoryClicked,
                            onMoreRepositoriesClicked = onMoreRepositoriesClicked,
                            onMoreTagClicked = onMoreTagClicked
                        )
                    }
                }

                composable("login") { _ ->
                    LoginPage(
                        modifier = Modifier.padding(it),
                        onCreditClicked = onCreditClicked
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoute.forEach { route ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(route.route::class)
                        } == true,
                        onClick = {
                            if (route.route == "admin") {
                                onAdminClick()
                                return@NavigationBarItem
                            }

                            navController.navigate(route.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = route.icon, contentDescription = stringResource(route.name)) },
                        label = { Text(stringResource(route.name)) },
                    )
                }
            }
        }
    )
}

data class TopLevelRoute<T : Any>(val name: StringResource, val route: T, val icon: ImageVector)
