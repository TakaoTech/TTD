package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.route.github.model.GHRepository
import com.takaotech.dashboard.route.github.model.GHUser
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import io.kotest.core.spec.style.FunSpec
import io.mockk.*


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
				)
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
				)
			)
		)

		coEvery { githubRepository.getAllStars() } returns testList

		every {
			depository.ghRepositoryExist(any())
		} returns false

		justRun { depository.saveRepositoriesToDB(any()) }

		controller.getStarsFromZeroAndStore()

		verify {
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
				)
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
				)
			)
		)

		coEvery { githubRepository.getAllStars() } returns testList.toList()

		every {
			depository.ghRepositoryExist(any())
		} returns true

		justRun { depository.saveRepositoriesToDB(any()) }

		controller.getStarsFromZeroAndStore()

		verify {
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
			)
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
				)
			),
			newObj
		)

		coEvery { githubRepository.getAllStars() } returns testList

		every {
			depository.ghRepositoryExist(1)
		} returns true

		every {
			depository.ghRepositoryExist(2)
		} returns false

		justRun { depository.saveRepositoriesToDB(any()) }

		controller.getStarsFromZeroAndStore()

		verify {
			depository.saveRepositoriesToDB(listOf(newObj))
		}
	}
})