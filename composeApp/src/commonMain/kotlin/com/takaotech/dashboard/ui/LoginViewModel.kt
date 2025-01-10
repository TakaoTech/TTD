package com.takaotech.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import com.takaotech.dashboard.model.jwt.TakaoSession
import com.takaotech.dashboard.ui.login.SessionManager
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

@Factory
class LoginViewModel(
    private val sessionManager: SessionManager,
) : ScreenModel {
    val takaoSession: StateFlow<TakaoSession?> = sessionManager.takaoSession

    fun startGoogleLogin() {
        sessionManager.startGoogleLogin()
    }

    fun startGoogleSignup() {
        sessionManager.startGoogleSignup()
    }

    fun logout() {
        sessionManager.logout()
    }
}
