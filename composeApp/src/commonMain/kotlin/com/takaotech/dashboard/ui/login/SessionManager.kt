package com.takaotech.dashboard.ui.login

import com.takaotech.dashboard.model.session.TokenPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializer
import org.koin.core.annotation.Singleton

interface SessionManager {
    //https://github.com/android/kotlin-multiplatform-samples/tree/main/DiceRoller

    //https://github.com/philipplackner/AndroidCrypto
    val sessionFlow: StateFlow<TokenPair?>


    fun startGoogleLogin()
    fun startGoogleSignup()
    fun logout()
}


