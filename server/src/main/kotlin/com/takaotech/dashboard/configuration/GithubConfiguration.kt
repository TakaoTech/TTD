package com.takaotech.dashboard.configuration

data class GithubConfiguration(
    val githubToken: String,
) {
    object Key {
        const val TOKEN = "github.token"
    }
}
