package com.takaotech.dashboard.configuration

data class CredentialConfig(
	val digestAlgorithm: String,
	val digest: String,
	val username: String,
	val password: String,
	val sessionConfig: SessionConfig
)

data class SessionConfig(
	val name: String,
	val realm: String
)
