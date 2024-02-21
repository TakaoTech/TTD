package com.takaotech.dashboard.repository

import org.koin.core.annotation.Single

@Single
class AuthRepository(
	private val authApi: AuthApi
) {

	fun login() {
		authApi.login()
	}
}