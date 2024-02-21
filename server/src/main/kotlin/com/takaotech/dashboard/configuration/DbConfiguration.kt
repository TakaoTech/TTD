package com.takaotech.dashboard.configuration

data class DbConfiguration(
	val url: String,
	val driver: String,
	val user: String = "",
	val password: String = ""
)