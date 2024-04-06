package com.takaotech.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.takaotech.dashboard.ui.LoginScreen
import com.takaotech.dashboard.ui.github.HomePageTab

object MainNavigator : Screen {
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow

		TabNavigator(HomePageTab) {
			Scaffold(
				content = {
					Box(Modifier.padding(it)) {
						CurrentScreen()
					}
				},
				bottomBar = {
					NavigationBar {
						TabNavigationItem(HomePageTab)
						TabNavigationItem(LoginScreen)
						NavigationBarItem(
							selected = false,
							onClick = {
								navigator.push(
									AdminNavigator
								)
							},
							icon = { Icon(Icons.Filled.AdminPanelSettings, "") }
						)
					}
				}
			)
		}
	}
}