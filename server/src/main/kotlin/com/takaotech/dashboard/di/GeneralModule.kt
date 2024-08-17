package com.takaotech.dashboard.di

import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.utils.GithubClientLoggerAdapter
import io.ktor.util.logging.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.extras.okhttp3.OkHttpGitHubConnector
import org.koin.core.module.Module
import org.koin.dsl.module

fun getGeneralModule(
    log: Logger,
): Module = module {
    single<Logger> {
        log
    }

    single {
        val githubConfiguration = get<GithubConfiguration>()

        GitHubBuilder().apply {
            withConnector(
                OkHttpGitHubConnector(
                    OkHttpClient.Builder()
                        .addInterceptor(
                            HttpLoggingInterceptor(
                                GithubClientLoggerAdapter(get())
                            ).apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }
                        )
                        .build()
                )
            )
            withOAuthToken(githubConfiguration.githubToken)
        }.build()
    }
}