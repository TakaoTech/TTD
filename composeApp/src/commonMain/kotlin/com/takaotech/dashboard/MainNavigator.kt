package com.takaotech.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.ui.LoginScreen
import com.takaotech.dashboard.ui.github.HomePageTab
import com.takaotech.dashboard.ui.login.SessionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object MainNavigator : Screen, KoinComponent {
    @Composable
    override fun Content() {
        val sessionManager by inject<SessionManager>()
        val session by sessionManager.takaoSession.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val showAdmin by remember(session) {
            derivedStateOf {
                session != null && session?.roles?.contains(TakaoRole.ADMINISTRATOR) == true
            }
        }

        TabNavigator(HomePageTab) {
            Scaffold(
                modifier = Modifier,
                content = {
                    Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
                        CurrentScreen()
                    }
                },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(HomePageTab)
                        TabNavigationItem(LoginScreen)
                        if (showAdmin) {
                            NavigationBarItem(
                                selected = false,
                                onClick = {
                                    navigator.push(
                                        AdminNavigator,
                                    )
                                },
                                icon = { Icon(Icons.Filled.AdminPanelSettings, "") },
                            )
                        }
                    }
                },
            )
        }
    }
}
