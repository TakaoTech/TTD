package com.takaotech.dashboard.ui.utils.logger

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import co.touchlab.kermit.Logger as KermitLogger

class KoinLoggerAdapter(private val kermitLogger: KermitLogger) : Logger() {
	override fun display(level: Level, msg: MESSAGE) {
		when (level) {
			Level.DEBUG -> {
				kermitLogger.d { msg }
			}

			Level.INFO -> {
				kermitLogger.i { msg }
			}

			Level.WARNING -> {
				kermitLogger.w { msg }
			}

			Level.ERROR -> {
				kermitLogger.e { msg }
			}

			Level.NONE -> Unit
		}
	}
}