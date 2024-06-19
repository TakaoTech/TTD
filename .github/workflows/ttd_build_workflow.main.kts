#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.1.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.docker.BuildPushActionV5
import io.github.typesafegithub.workflows.actions.docker.LoginActionV3
import io.github.typesafegithub.workflows.actions.docker.SetupBuildxActionV3
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradleV3
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

val ACT by Contexts.env

val DOCKER_HUB_USERNAME = "vars.DOCKER_HUB_USERNAME"
val DOCKER_HUB_TOKEN by Contexts.secrets

workflow(
    name = "Server build workflow",
    on = listOf(
        Push(
            branches = listOf("test")
        )
    ),
    sourceFile = __FILE__,
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled
) {
    job(
        id = "build_server",
        runsOn = UbuntuLatest,
    ) {
        uses(
            name = "Setup Java",
            action = SetupJavaV4(javaVersion = "17", distribution = SetupJavaV4.Distribution.Corretto)
        )

        uses(
            name = "Set up Docker Buildx",
            action = SetupBuildxActionV3()
        )

        uses(
            name = "Setup Gradle",
            action = ActionsSetupGradleV3()
        )

        uses(
            name = "Checkout",
            action = CheckoutV4()
        )

//		run(name ="Change permission for Act execution", command = "chmod +x -R *", condition = expr { ACT })

        uses(
            name = "Login to DockerHub",
            action = LoginActionV3(
                username = expr(DOCKER_HUB_USERNAME),
                password = expr { DOCKER_HUB_TOKEN }
            ),
        )

        uses(
            name = "Generate Image",
            action = BuildPushActionV5(
                platforms = listOf("linux/amd64"),
                file = "./Dockerfile",
                push = true,
                tags = listOf("samuele794/ttd:test"),
            )
        )

//		run(name = "Generate image", command = "./gradlew server:publishImage")
    }
}