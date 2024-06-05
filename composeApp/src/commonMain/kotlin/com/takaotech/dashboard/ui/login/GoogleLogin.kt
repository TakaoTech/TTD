package com.takaotech.dashboard.ui.login

import com.github.kittinunf.result.Result

interface GoogleLogin {
    suspend fun startLogin(): Result<Pair<Nonce, GoogleToken>, Exception>
}

typealias Nonce = String
typealias GoogleToken = String