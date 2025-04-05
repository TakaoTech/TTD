package com.takaotech.dashboard

import com.takaotech.dashboard.di.platformModules
import org.koin.core.context.startKoin

fun startTakaoApplicationKoin() {
    startKoin {
        printLogger()
        platformModules()
    }
}