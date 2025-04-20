package com.takaotech.dashboard

import com.takaotech.dashboard.di.platformModules
import com.takaotech.dashboard.ui.platform.SymmetricCryptoManager
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun startTakaoApplicationKoin(
    cryptoManager: SymmetricCryptoManager
) {
    startKoin {
        printLogger()
        platformModules()
        modules(
            module {
                single {
                    cryptoManager
                }
            }
        )
    }
}