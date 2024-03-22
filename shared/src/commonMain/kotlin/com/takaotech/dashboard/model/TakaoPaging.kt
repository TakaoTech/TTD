package com.takaotech.dashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TakaoPaging<T>(
	@SerialName("data")
	val data: List<T>,
	@SerialName("page")
	val page: Int,
	@SerialName("totalPage")
	val totalPage: Long
)