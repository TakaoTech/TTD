package com.takaotech.dashboard.utils

import io.ktor.util.logging.*
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpLoggerAdapter(val logger: Logger) : HttpLoggingInterceptor.Logger {
	override fun log(message: String) {
		logger.debug(message)
	}
}