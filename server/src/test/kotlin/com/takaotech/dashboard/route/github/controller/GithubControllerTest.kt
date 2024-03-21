package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHUser
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import com.takaotech.dashboard.route.github.repository.TagsRepository
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlin.test.assertEquals


class GithubControllerTest : FunSpec({
	var githubRepository = mockk<GithubRepository>()
	var depository = mockk<DepositoryRepository>()
	var tagsRepository = mockk<TagsRepository>()
	var controller = GithubController(githubRepository, depository, tagsRepository)

	beforeEach {
		githubRepository = mockk<GithubRepository>()
		depository = mockk<DepositoryRepository>()
		tagsRepository = mockk<TagsRepository>()
		controller = GithubController(githubRepository, depository, tagsRepository)
	}

	test("Insert new data, all data inserted") {
		val testList = listOf(
			GHRepositoryDao(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
			),
			GHRepositoryDao(
				id = 2,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
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
			GHRepositoryDao(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
			),
			GHRepositoryDao(
				id = 2,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
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
		val newObj = GHRepositoryDao(
			id = 2,
			name = "Kotlin",
			fullName = "Kotlin",
			description = "Test Description",
			url = "https://www.bing.com/search?q=kotlin",
			license = "Apache 2",
			licenseUrl = "https://test.com",
			user = GHUser(
				id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
			),
			languages = listOf(
				GHLanguageDao("Kotlin", 100)
			),
			tags = listOf(),
			mainCategory = MainCategory.KOTLIN,
			updatedAt = Clock.System.now()
		)
		val testList = listOf(
			GHRepositoryDao(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
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
			GHRepositoryDao(
				id = 1,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
			),
			GHRepositoryDao(
				id = 2,
				name = "Kotlin",
				fullName = "Kotlin",
				description = "Test Description",
				url = "https://www.bing.com/search?q=kotlin",
				license = "Apache 2",
				licenseUrl = "https://test.com",
				user = GHUser(
					id = 1, name = "Kotlin", url = "https://duckduckgo.com/?q=kotlin"
				),
				languages = listOf(
					GHLanguageDao("Kotlin", 100)
				),
				tags = listOf(),
				mainCategory = MainCategory.KOTLIN,
				updatedAt = Clock.System.now()
			)
		)

		coEvery { depository.getGHRepository() } returns testList

		assertEquals(testList, controller.getRepository())
	}
})