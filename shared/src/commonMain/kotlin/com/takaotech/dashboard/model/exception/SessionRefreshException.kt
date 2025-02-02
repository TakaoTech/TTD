package com.takaotech.dashboard.model.exception

class SessionRefreshException(message: String) : Exception("Session refresh failed: $message")