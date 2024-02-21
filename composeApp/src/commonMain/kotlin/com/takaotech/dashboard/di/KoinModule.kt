package com.takaotech.dashboard.di

import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.repository.AuthApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun getApiModule(baseUrl: String) = module {
	single { HttpClient() }

	single {
		Ktorfit.Builder()
			.baseUrl(baseUrl)
			.httpClient(get<HttpClient>())
			.build()
	}

	single {
		get<Ktorfit>().create<AuthApi>()
	}
}

fun appModules() = getApiModule(AppBuildKonfig.baseUrl)

expect fun KoinApplication.platformModules()