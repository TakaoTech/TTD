package com.takaotech.dashboard.di

import com.takaotech.dashboard.ui.login.GoogleLogin
import com.takaotech.dashboard.ui.login.GoogleLoginImpl
import com.takaotech.dashboard.ui.login.SessionManager
import com.takaotech.dashboard.ui.login.SessionManagerImpl
import org.koin.core.KoinApplication
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

@Module
@ComponentScan("com.takaotech.dashboard.android")
class AndroidModule

actual fun KoinApplication.platformModules() {
    modules(
        defaultModule,
        module {
            single { GoogleLoginImpl(get()) } bind (GoogleLogin::class)
            single {
                SessionManagerImpl(
                    googleLogin = get(),
                    authApi = get(),
                    context = get()
                )
            } bind SessionManager::class withOptions {
                createdAtStart()
            }

        },
        *appModules(),
    )
}