package com.takaotech.dashboard

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.transitions.SlideTransition
import com.takaotech.dashboard.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        // TODO Support back gesture https://github.com/adrielcafe/voyager/issues/223
        // TODO Supporto RTL Slide Transition
        Navigator(MainNavigator) {
            val layoutDirection = LocalLayoutDirection.current
            if (layoutDirection == LayoutDirection.Ltr) {
            } else {
            }

            SlideTransition(navigator = it)
        }
    }
}

@Composable
fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
        label = { Text(tab.options.title) },
    )
}
