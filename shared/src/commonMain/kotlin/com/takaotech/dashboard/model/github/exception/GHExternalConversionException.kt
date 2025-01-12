package com.takaotech.dashboard.model.github.exception

class GHExternalConversionException(
    val id: String,
    val name: String,
    val property: String,
    cause: Throwable,
) : Throwable("Error convert repository $id $name Property: $property", cause)