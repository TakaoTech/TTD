package com.takaotech.dashboard.di

import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.repository.api.AdminGHApi
import com.takaotech.dashboard.repository.api.GHApi
import com.takaotech.dashboard.ui.login.SessionManager
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import co.touchlab.kermit.Logger as KermitLogger


fun getApiModule(baseUrl: String) = module {

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single(named("AuthKtor")) {
        Ktorfit.Builder()
            .baseUrl(baseUrl)
            .httpClient(getBaseKtor(get<KermitLogger>()))
            .build()
    }

    single {
        val sessionManager = get<SessionManager>()
        val baseKtor = getBaseKtor(get<KermitLogger>()).config {
            Auth {

            }
        }
        sessionManager.bindKtor(baseKtor)
        baseKtor
    }

    single {
        Ktorfit.Builder()
            .baseUrl(baseUrl)
            .httpClient(get<HttpClient>())
            .build()
    }

    single {
        get<Ktorfit>(named("AuthKtor")).create<AuthApi>()
    }

    single {
        get<Ktorfit>().create<AdminGHApi>()
    }

    single {
        get<Ktorfit>().create<GHApi>()
    }
}

private fun getBaseKtor(
    kermitLogger: KermitLogger
): HttpClient {
    return HttpClient {
        install(HttpTimeout)
        install(ContentNegotiation) {
            json()
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    kermitLogger.largeLog(message)
                }
            }

            level = LogLevel.ALL
        }

        defaultRequest {
            headers {
                contentType(ContentType.Application.Json)
            }
        }
    }
}

private const val _charLimit = 2000
fun KermitLogger.largeLog(message: String) {

    if (message.length < _charLimit) {
        return d { message }
    }
    val sections = message.length / _charLimit
    for (i in 0..sections) {
        val max = _charLimit * (i + 1)
        if (max >= message.length) {
            d { message.substring(_charLimit * i) }
        } else {
            d { message.substring(_charLimit * i, max) }
        }
    }
}

fun commonModule() = module {
    single {
        KermitLogger(
            config = loggerConfigInit(platformLogWriter()),
            tag = "TTDApp"
        )
    }
}

fun appModules() = arrayOf(getApiModule(AppBuildKonfig.baseUrl), commonModule())

expect fun KoinApplication.platformModules()