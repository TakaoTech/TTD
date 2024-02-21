package com.takaotech.dashboard.repository

import de.jensklingenberg.ktorfit.http.GET

interface AuthApi {
	@GET("/login")
	fun login()
}