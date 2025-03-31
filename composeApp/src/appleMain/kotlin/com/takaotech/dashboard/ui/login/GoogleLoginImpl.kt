package com.takaotech.dashboard.ui.login

import com.github.kittinunf.result.Result

class GoogleLoginImpl() : GoogleLogin {
    override suspend fun startLogin(): Result<Pair<Nonce, GoogleToken>, Exception> {
        TODO("Not yet implemented")
    }
}