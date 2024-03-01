package com.takaotech.dashboard.di

import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.repository.api.GHApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun getApiModule(baseUrl: String) = module {
	single {
		HttpClient {
			install(ContentNegotiation) {
				json()
			}
		}
	}

	single {
		Ktorfit.Builder()
			.baseUrl(baseUrl)
			.httpClient(get<HttpClient>())
			.build()
	}

	single {
		get<Ktorfit>().create<AuthApi>()
	}

	single {
		get<Ktorfit>().create<GHApi>()
	}
}

fun appModules() = getApiModule(AppBuildKonfig.baseUrl)

expect fun KoinApplication.platformModules()