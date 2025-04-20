package com.takaotech.dashboard.di

import co.touchlab.kermit.Logger
import com.takaotech.dashboard.ui.login.GoogleLogin
import com.takaotech.dashboard.ui.login.GoogleLoginImpl
import com.takaotech.dashboard.ui.login.SessionManager
import com.takaotech.dashboard.ui.login.SessionManagerImpl
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

@Suppress("SpreadOperator")
actual fun KoinApplication.platformModules() {
    modules(
        defaultModule,
        module {
            single { GoogleLoginImpl() } bind (GoogleLogin::class)
            single {
                SessionManagerImpl(
                    json = get(),
                    logger = get(),
                    googleLogin = get(),
                    authApi = get(),
                    cryptoManager = get()
                ).apply {
                    init()
                }
            } bind SessionManager::class withOptions {
                createdAtStart()
            }
        },
        *appModules(),
    )
}

internal actual fun getBaseKtor(kermitLogger: Logger): HttpClient =
    HttpClient(Darwin) {
        configureCommonHttp(kermitLogger)
    }
