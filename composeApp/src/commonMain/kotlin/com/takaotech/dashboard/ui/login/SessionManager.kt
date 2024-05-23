package com.takaotech.dashboard.ui.login

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializer
import org.koin.core.annotation.Singleton

interface SessionManager {
    //https://github.com/android/kotlin-multiplatform-samples/tree/main/DiceRoller

    //https://github.com/philipplackner/AndroidCrypto
    val sessionFlow: Flow<String?>


    fun startGoogleLogin()
    fun logout()
}


