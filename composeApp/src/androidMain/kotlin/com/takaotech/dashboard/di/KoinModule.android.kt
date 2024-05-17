package com.takaotech.dashboard.di

import com.takaotech.dashboard.ui.login.GoogleLogin
import com.takaotech.dashboard.ui.login.GoogleLoginImpl
import org.koin.core.KoinApplication
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.scope.get
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

@Module
@ComponentScan("com.takaotech.dashboard.android")
class AndroidModule

actual fun KoinApplication.platformModules() {
    modules(
        defaultModule,
        *appModules(),
        module {
            single { GoogleLoginImpl(get()) } bind(GoogleLogin::class)
        }
    )
}