package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.GoogleJwtConfig
import com.takaotech.dashboard.configuration.GoogleOauth2Config
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.configuration.TakaoJwtConfig
import com.takaotech.dashboard.mainModule
import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.models.GHRepositoryMiniDao
import com.takaotech.dashboard.route.github.controller.GithubController
import installPostgres
import installRedis
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GithubRouterTest : FunSpec() {

    init {
        val githubController: GithubController = mockk()

        val redis = installRedis()
        val postgres = installPostgres()

        xcontext("GithubRoute") {
            test("tagId null") {
                // TODO
                val testPaging = TakaoPaging<GHRepositoryMiniDao>(data = listOf(), page = 1, totalPage = 0)

                coEvery { githubController.getRepositoryMini(any(), any()) } returns testPaging

                testApplication {
                    environment {
                        //TODO Need set mock environment

                        config = MapApplicationConfig(
                            listOf(
                                listOf(
                                    listOf(
                                        SqlDbConfiguration.Keys.URL to postgres.jdbcUrl,
                                        //TODO Commonize
                                        SqlDbConfiguration.Keys.DRIVER to "org.postgresql.Driver",
                                        SqlDbConfiguration.Keys.USER to postgres.username,
                                        SqlDbConfiguration.Keys.PASSWORD to postgres.password,
                                    ),
                                    listOf(
                                        RedisConfiguration.Keys.URL to redis.redisURI,
                                    )
                                ).flatten(),
                                listOf(
                                    //TODO Mock APIS
                                    GithubConfiguration.Key.TOKEN to "testGHToken"
                                ),
                                listOf(
                                    listOf(
                                        GoogleOauth2Config.Keys.REDIRECT to "googleRedirect",
                                        GoogleOauth2Config.Keys.CLIENT_ID to "CLIENT_ID",
                                        GoogleOauth2Config.Keys.CLIENT_SECRET to "CLIENT_SECRET"
                                    ),
                                    listOf(
                                        GoogleJwtConfig.Keys.ISSUER to "ISSUER",
                                        GoogleJwtConfig.Keys.AUDIENCE to "AUDIENCE",
                                    ),
                                    listOf(
                                        TakaoJwtConfig.Keys.VERSION to "1",
                                        TakaoJwtConfig.Keys.SECRET to "SECRET",
                                        TakaoJwtConfig.Keys.ISSUER to "ISSUER",
                                        TakaoJwtConfig.Keys.AUDIENCE to "AUDIENCE",
                                        TakaoJwtConfig.Keys.REALM to "REALM",
                                        TakaoJwtConfig.Keys.ACCESS_LIFETIME to "10m",
                                        TakaoJwtConfig.Keys.REFRESH_LIFETIME to "10m",
                                    )
                                ).flatten()
                            ).flatten()
                        )
                    }

                    application {
                        mainModule()
                    }

                    val response = client.get("/github?page=1&size=10")
                    response shouldHaveStatus HttpStatusCode.OK
                    response.bodyAsText() shouldBe Json.encodeToString(testPaging)
                }
            }
        }
    }
}
