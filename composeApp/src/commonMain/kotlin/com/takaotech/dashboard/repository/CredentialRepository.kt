package com.takaotech.dashboard.repository

expect class CredentialRepository {

	fun saveCredential(username: String, password: String)
}