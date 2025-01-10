package com.takaotech.dashboard.utils

import io.ktor.util.logging.*
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.MarkerFactory

class GithubClientLoggerAdapter(
    val logger: Logger,
) : HttpLoggingInterceptor.Logger {
    private val marker = MarkerFactory.getMarker("GithubClient")

    override fun log(message: String) {
        logger.trace(marker, message)
    }
}
