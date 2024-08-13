package com.takaotech.dashboard.di

import co.touchlab.kermit.Logger
import io.ktor.client.*
import org.koin.core.KoinApplication

actual fun KoinApplication.platformModules() {
}

internal actual fun getBaseKtor(kermitLogger: Logger): HttpClient {
    TODO("Not yet implemented")
}