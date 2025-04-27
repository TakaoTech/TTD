package com.takaotech.dashboard.di

import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.utils.GithubClientLoggerAdapter
import com.takaotech.dashboard.utils.OkHttpRequestResponseFileLogInterceptor
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.logging.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.extras.okhttp3.OkHttpGitHubConnector
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun getGeneralModule(
    log: Logger,
    developmentMode: Boolean = false
): Module =
    module {
        single<Logger> {
            log
        }

        single {
            val githubConfiguration = get<GithubConfiguration>()

            GitHubBuilder()
                .apply {
                    withConnector(
                        OkHttpGitHubConnector(
                            OkHttpClient
                                .Builder().apply {
                                    if (developmentMode) {
                                        addInterceptor(OkHttpRequestResponseFileLogInterceptor())
                                    }
                                }
                                .addInterceptor(
                                    HttpLoggingInterceptor(
                                        GithubClientLoggerAdapter(get()),
                                    ).apply {
                                        level = HttpLoggingInterceptor.Level.BODY
                                    },
                                ).build(),
                        ),
                    )
                    withOAuthToken(githubConfiguration.githubToken)
                }.build()
        }

        factory(named(HTTP_JSON_CLIENT)) {
            HttpClient(Java) {
                engine {
                    protocolVersion = java.net.http.HttpClient.Version.HTTP_2
                }
                install(ContentNegotiation) {
                    json()
                }
            }
        }
    }

const val HTTP_JSON_CLIENT = "HTTP_JSON_CLIENT"
