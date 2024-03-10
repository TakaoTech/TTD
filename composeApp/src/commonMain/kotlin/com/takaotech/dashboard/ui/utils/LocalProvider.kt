package com.takaotech.dashboard.ui.utils

fun noLocalProvidedFor(name: String): Nothing {
	error("CompositionLocal $name not present")
}