package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.model.GHRepository
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.assertEquals


class GithubControllerTest : FunSpec({
	var githubRepository = mockk<GithubRepository>()
	var depository = mockk<DepositoryRepository>()
	var controller = GithubController(githubRepository, depository)
	beforeEach {
		githubRepository = mockk<GithubRepository>()
		depository = mockk<DepositoryRepository>()
		controller = GithubController(githubRepository, depository)
	}

	test("Insert new data, all data inserted") {
		val testList = listOf(
			GHRepository(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			),
			GHRepository(
				id = 2,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			)
		)

		coEvery { githubRepository.getAllStars() } returns testList

		coEvery {
			depository.ghRepositoryExist(any())
		} returns false

		coJustRun { depository.saveRepositoriesToDB(any()) }

		controller.getStarsFromZeroAndStore()

		coVerify {
			depository.saveRepositoriesToDB(testList)
		}
	}

	test("Insert new data, no data inserted because exist") {
		val testList = listOf(
			GHRepository(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			),
			GHRepository(
				id = 2,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			)
		)

		coEvery { githubRepository.getAllStars() } returns testList.toList()

		coEvery {
			depository.ghRepositoryExist(any())
		} returns true

		coJustRun { depository.saveRepositoriesToDB(any()) }

		controller.getStarsFromZeroAndStore()

		coVerify {
			depository.saveRepositoriesToDB(emptyList())
		}
	}

	test("Insert new data, filter ID=1 because exist") {
		val newObj = GHRepository(
			id = 2,
			name = "Kotlin",
			fullName = "Kotlin",
			url = "https://www.bing.com/search?q=kotlin",
			user = GHUser(
				id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
			),
			languages = mapOf(
				"Kotlin" to 100
			),
			tags = listOf(),
			mainCategory = MainCategory.KOTLIN
		)
		val testList = listOf(
			GHRepository(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			),
			newObj
		)

		coEvery { githubRepository.getAllStars() } returns testList

		coEvery {
			depository.ghRepositoryExist(1)
		} returns true

		coEvery {
			depository.ghRepositoryExist(2)
		} returns false

		coJustRun { depository.saveRepositoriesToDB(any()) }

		controller.getStarsFromZeroAndStore()

		coEvery {
			depository.saveRepositoriesToDB(listOf(newObj))
		}
	}

	test("Get Stored Depository, not empty") {
		val testList = listOf(
			GHRepository(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			),
			GHRepository(
				id = 2,
				name = "Kotlin",
				fullName = "Kotlin",
				url = "https://www.bing.com/search?q=kotlin",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = mapOf(
					"Kotlin" to 100
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN
			)
		)

		coEvery { depository.getDepository() } returns testList

		assertEquals(testList, controller.getStoredRepository())
	}
})