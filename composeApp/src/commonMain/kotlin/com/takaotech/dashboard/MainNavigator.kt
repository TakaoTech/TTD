package com.takaotech.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
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
					BottomNavigation {
						TabNavigationItem(HomePageTab)
						TabNavigationItem(LoginScreen)
					}
					IconButton(
						onClick = {
							navigator.push(
								AdminNavigator
							)
						}
					) {
						Icon(Icons.Filled.AdminPanelSettings, "")
					}
				}
			)
		}
	}
}