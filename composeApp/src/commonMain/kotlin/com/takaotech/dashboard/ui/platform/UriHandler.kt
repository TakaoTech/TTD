package com.takaotech.dashboard.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf
import com.takaotech.dashboard.ui.utils.noLocalProvidedFor

interface UriHandler {
	fun openUrl(url: String)
}

val LocalTTDUriHandler = staticCompositionLocalOf<UriHandler> { noLocalProvidedFor("LocalTTDUriHandler") }