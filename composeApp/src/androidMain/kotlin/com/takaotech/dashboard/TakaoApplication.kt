package com.takaotech.dashboard

import android.app.Application
import com.takaotech.dashboard.di.platformModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TakaoApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(this@TakaoApplication)
			androidLogger()
			platformModules()
		}
	}
}