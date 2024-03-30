package com.takaotech.dashboard

import android.app.Application
import co.touchlab.kermit.Logger
import com.takaotech.dashboard.di.platformModules
import com.takaotech.dashboard.ui.utils.logger.KoinLoggerAdapter
import io.kotzilla.cloudinject.CloudInjectSDK
import io.kotzilla.cloudinject.analytics.koin.analyticsLogger
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
class TakaoApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		CloudInjectSDK.setup(this)

		startKoin {
			androidContext(this@TakaoApplication)
			platformModules()
			analyticsLogger(KoinLoggerAdapter(get<Logger>()))
			androidLogger()
		}
	}
}