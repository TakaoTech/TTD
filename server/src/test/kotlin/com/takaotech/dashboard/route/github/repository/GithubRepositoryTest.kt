package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import com.takaotech.dashboard.utils.LOGGER
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.test.KoinTest
import java.net.URL
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.kohsuke.github.GHRepository as GHRepositoryExternal
import org.kohsuke.github.GHUser as GHUserExternal

class GithubRepositoryTest : FunSpec({
	var githubClient = mockk<GithubClientInterface>()

	var githubRepository = GithubRepository(
		LOGGER,
		githubClient
	)

	beforeEach {
		githubClient = mockk<GithubClientInterface>()

		githubRepository = GithubRepository(
			LOGGER,
			githubClient
		)
	}


	test("Get Repository Start List") {
		val testList = listOf(
			GHRepositoryDao(
				id = 3041,
				name = "Margery Miles",
				fullName = "Taylor Alford",
				description = null,
				url = "http://www.bing.com/search?q=vivamus",
				license = null,
				licenseUrl = null,
				user = GHUser(
					id = 9763,
					name = "Delmar Bowers",
					url = "https://duckduckgo.com/?q=pellentesque"
				),
				languages = mapOf(),
				tags = listOf(),
				mainCategory = MainCategory.OTHER,
				updatedAt = Clock.System.now()
			)
		)

		coEvery { githubClient.getAllStarsRemote() } returns testList

		val repoStars = githubRepository.getAllStars()
		assertTrue { repoStars.isNotEmpty() }
		assertEquals(testList, repoStars)
	}

	test("Get Repository Start List Empty") {
		val testList = listOf<GHRepositoryDao>()

		coEvery { githubClient.getAllStarsRemote() } returns testList

		val repoStars = githubRepository.getAllStars()
		assertTrue { repoStars.isEmpty() }
		assertEquals(testList, repoStars)
	}

	test("Test getRepositoryLanguages filled") {
		coEvery { githubClient.getLanguagesByRepository(any()) } returns mapOf("Kotlin" to 100L, "Java" to 100L)

		val languagesList = githubRepository.getRepositoryLanguages(0)

		assertTrue { languagesList.isNotEmpty() }
		assertEquals(mapOf("Kotlin" to 100L, "Java" to 100L), languagesList)
	}

	test("Test convertToGHRepositoryWithDefaults field all filled") {
		val ghExternalMockk = mockk<GHRepositoryExternal>()

		val ghUserExternalMockk = mockk<GHUserExternal>()
		//region GHUserExternal mockk
		every { ghUserExternalMockk.id } returns 123L
		every { ghUserExternalMockk.login } returns "TakaoTech"
		every { ghUserExternalMockk.url } returns URL("https://duckduckgo.com/?q=pellentesque")
		//endregion GHUserExternal mockk

		every { ghExternalMockk.id } returns 456L
		every { ghExternalMockk.owner } returns ghUserExternalMockk
		every { ghExternalMockk.name } returns "Kotlin"
		every { ghExternalMockk.fullName } returns "Jetbrains/Kotlin"
		every { ghExternalMockk.description } returns "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Excepteur kasd"
		every { ghExternalMockk.htmlUrl } returns URL("https://duckduckgo.com/?q=takaotech")
		every { ghExternalMockk.license.name } returns "Apache 2.0"
		every { ghExternalMockk.license.htmlUrl } returns URL("https://duckduckgo.com/?q=apache2License")
		every { ghExternalMockk.updatedAt } returns Date.from(java.time.Instant.parse("2011-01-26T19:14:43Z"))
		every { ghExternalMockk.listLanguages() } returns mapOf("Kotlin" to 100L)

		val convertedMockk = ghExternalMockk.convertToGHRepositoryWithDefaults()!!

		convertedMockk.user.let { user ->
			assertEquals(123L, user.id)
			assertEquals("TakaoTech", user.name)
			assertEquals("https://duckduckgo.com/?q=pellentesque", user.url)
		}

		assertEquals(456L, convertedMockk.id)
		assertEquals("Kotlin", convertedMockk.name)
		assertEquals("Jetbrains/Kotlin", convertedMockk.fullName)
		assertEquals(
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Excepteur kasd",
			convertedMockk.description
		)
		assertEquals("https://duckduckgo.com/?q=takaotech", convertedMockk.url)
		assertEquals("Apache 2.0", convertedMockk.license)
		assertEquals("https://duckduckgo.com/?q=apache2License", convertedMockk.licenseUrl)
		assertEquals(mapOf("Kotlin" to 100L), convertedMockk.languages)
		assertEquals(MainCategory.NONE, convertedMockk.mainCategory)
		assertEquals(Instant.parse("2011-01-26T19:14:43Z"), convertedMockk.updatedAt)
		assertTrue { convertedMockk.tags.isEmpty() }
	}

	test("Test convertToGHRepositoryWithDefaults field licence null") {
		val ghExternalMockk = mockk<GHRepositoryExternal>()

		val ghUserExternalMockk = mockk<GHUserExternal>()
		//region GHUserExternal mockk
		every { ghUserExternalMockk.id } returns 123L
		every { ghUserExternalMockk.login } returns "TakaoTech"
		every { ghUserExternalMockk.url } returns URL("https://duckduckgo.com/?q=pellentesque")
		//endregion GHUserExternal mockk

		every { ghExternalMockk.id } returns 456L
		every { ghExternalMockk.owner } returns ghUserExternalMockk
		every { ghExternalMockk.name } returns "Kotlin"
		every { ghExternalMockk.fullName } returns "Jetbrains/Kotlin"
		every { ghExternalMockk.description } returns "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Excepteur kasd"
		every { ghExternalMockk.htmlUrl } returns URL("https://duckduckgo.com/?q=takaotech")
		every { ghExternalMockk.license } returns null
		every { ghExternalMockk.updatedAt } returns Date.from(java.time.Instant.parse("2011-01-26T19:14:43Z"))
		every { ghExternalMockk.listLanguages() } returns mapOf("Kotlin" to 100L)

		val convertedMockk = ghExternalMockk.convertToGHRepositoryWithDefaults()!!

		convertedMockk.user.let { user ->
			assertEquals(123L, user.id)
			assertEquals("TakaoTech", user.name)
			assertEquals("https://duckduckgo.com/?q=pellentesque", user.url)
		}

		assertEquals(456L, convertedMockk.id)
		assertEquals("Kotlin", convertedMockk.name)
		assertEquals("Jetbrains/Kotlin", convertedMockk.fullName)
		assertEquals(
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Excepteur kasd",
			convertedMockk.description
		)
		assertEquals("https://duckduckgo.com/?q=takaotech", convertedMockk.url)
		assertEquals(null, convertedMockk.license)
		assertEquals(null, convertedMockk.licenseUrl)
		assertEquals(mapOf("Kotlin" to 100L), convertedMockk.languages)
		assertEquals(MainCategory.NONE, convertedMockk.mainCategory)
		assertTrue { convertedMockk.tags.isEmpty() }
		assertEquals(Instant.parse("2011-01-26T19:14:43Z"), convertedMockk.updatedAt)
	}

	test("Test convertToGHRepositoryWithDefaults user null return GHRepository null") {
		val ghExternalMockk = mockk<GHRepositoryExternal>()

		every { ghExternalMockk.id } returns 456L
		every { ghExternalMockk.owner } returns null
		every { ghExternalMockk.name } returns "Kotlin"
		every { ghExternalMockk.fullName } returns "Jetbrains/Kotlin"
		every { ghExternalMockk.description } returns "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Excepteur kasd"
		every { ghExternalMockk.url } returns URL("https://duckduckgo.com/?q=takaotech")
		every { ghExternalMockk.license.name } returns "Apache 2.0"
		every { ghExternalMockk.license.htmlUrl } returns URL("https://duckduckgo.com/?q=apache2License")
		every { ghExternalMockk.listLanguages() } returns mapOf("Kotlin" to 100L)

		val convertedMockk = ghExternalMockk.convertToGHRepositoryWithDefaults()
		assertTrue { convertedMockk == null }
	}
}), KoinTest