package com.takaotech.dashboard.ui.utils

import app.cash.paging.*
import com.takaotech.dashboard.model.TakaoPaging

abstract class BasePagingSource<Value : Any> : PagingSource<Int, Value>() {
	//https://medium.com/@asia_sama/paging-3-with-kmp-kotlin-multiplatform-811541c0f297
	protected abstract suspend fun fetchData(page: Int, limit: Int): TakaoPaging<Value>

	override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Value> {
		val currentPage = params.key ?: 1
		val limit = params.loadSize
		return try {
			val response = fetchData(currentPage, limit)
			PagingSourceLoadResultPage(
				data = response.data,
				prevKey = if (currentPage == 1) null else currentPage - 1,
				nextKey = (currentPage + 1).takeIf { response.page < response.totalPage }
			)

		} catch (e: Exception) {
			PagingSourceLoadResultError(e)
		}
	}

	override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
		return state.anchorPosition
	}

}