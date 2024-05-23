package com.takaotech.dashboard.ui.login

interface GoogleLogin {
    suspend fun startLogin(): String?
}