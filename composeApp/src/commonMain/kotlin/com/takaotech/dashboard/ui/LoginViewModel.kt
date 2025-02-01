package com.takaotech.dashboard.ui

import androidx.lifecycle.ViewModel
import com.takaotech.dashboard.model.jwt.TakaoSession
import com.takaotech.dashboard.ui.login.SessionManager
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {
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
