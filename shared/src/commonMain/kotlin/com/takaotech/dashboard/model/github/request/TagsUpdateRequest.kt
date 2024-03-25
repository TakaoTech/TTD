package com.takaotech.dashboard.model.github.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagsUpdateRequest(
	@SerialName("tagIds")
	val tagIds: List<Int>
)
