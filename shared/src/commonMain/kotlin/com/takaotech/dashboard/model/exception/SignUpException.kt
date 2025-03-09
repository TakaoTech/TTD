package com.takaotech.dashboard.model.exception

sealed class SignUpException(message: String) : Exception(message) {
    class EmailNotVerified : SignUpException("Email not verified")
    class EmailNotFound : SignUpException("Email not found")
    class UserAlreadyExists : SignUpException("User already exists")
    class InvalidUserData(field: String) : SignUpException("Invalid user data: $field missing")
}