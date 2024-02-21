package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.SessionConfig
import com.takaotech.dashboard.di.getGeneralModule
import com.takaotech.dashboard.utils.LOGGER
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertTrue

class GithubRepositoryTest : FunSpec(), KoinTest {
	override fun extensions() = listOf(
		KoinExtension(
			listOf(
				getGeneralModule(
					LOGGER,
					dbConfiguration = DbConfiguration(
						url = System.getenv("DB_URL"),
						driver = System.getenv("DB_DRIVER"),
						user = System.getenv("DB_USER"),
						password = System.getenv("DB_PASSWORD")
					),
					githubConfiguration = GithubConfiguration(
						githubToken = System.getenv("GITHUB_TOKEN")
					),
					credentialConfig = CredentialConfig(
						digestAlgorithm = System.getenv("auth.digest.alg"),
						digest = System.getenv("auth.digest"),
						username = System.getenv("auth.username"),
						password = System.getenv("auth.password"),
						sessionConfig = SessionConfig(
							name = System.getenv("auth.session.name"),
							realm = System.getenv("auth.session.realm")
						)
					)
				), defaultModule
			)
		)
	)

	val githubRepository by inject<GithubRepository>()

	init {
		test("Get Repository Start List") {
			val repoStars = githubRepository.getAllStars()
			assertTrue { repoStars.isNotEmpty() }
		}

		test("Get Languages of Repository") {
			//It's Ktofit ID
			val languages = githubRepository.getLanguagesByRepository(203655484)
			LOGGER.info(Json.encodeToString(languages))
			assertTrue { languages.isNotEmpty() }
		}
	}

}