package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.utils.installPostgres
import com.takaotech.dashboard.utils.installRedis
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import org.koin.test.KoinTest

class GithubRouterIntegrationTest : FunSpec(), KoinTest {
    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential


    init {
        installPostgres()
        installRedis()
    }
}
