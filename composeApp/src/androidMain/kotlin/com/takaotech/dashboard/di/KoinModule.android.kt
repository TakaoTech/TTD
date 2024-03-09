package com.takaotech.dashboard.di

import org.koin.core.KoinApplication
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.defaultModule

@Module
@ComponentScan("com.takaotech.dashboard.android")
class AndroidModule

actual fun KoinApplication.platformModules() {
	modules(defaultModule, *appModules())
}