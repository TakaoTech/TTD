package com.takaotech.dashboard.ui.platform

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent


class AndroidUriHandler(private val context: Context) : UriHandler {
	override fun openUrl(url: String) {
		val intent = CustomTabsIntent.Builder()
			.build()
		intent.launchUrl(context, Uri.parse(url))
	}
}