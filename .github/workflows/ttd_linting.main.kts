#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.1.0")
@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:actions__setup-gradle:v4")
@file:DependsOn("stefanzweifel:git-auto-commit-action:v5")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.stefanzweifel.GitAutoCommitAction
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

val ACT by Contexts.env

workflow(
    name = "Lint Check",
    on = listOf(
        Push(),
        PullRequest(
            types = listOf(
                PullRequest.Type.ReadyForReview,
                PullRequest.Type.ReviewRequested
            )
        )
    ),
    sourceFile = __FILE__,
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled
) {
    job(
        id = "lint",
        runsOn = UbuntuLatest,
    ) {
        uses(
            name = "Setup Java",
            action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Corretto)
        )

        uses(
            name = "Setup Gradle",
            action = ActionsSetupGradle()
        )

        uses(
            name = "Checkout",
            action = Checkout()
        )

        run(
            name = "Change permission for Act execution",
            command = "chmod +x -R *",
            condition = expr { "github.event.act" }
        )

        run(
            name = "Lint Fix",
            command = "./gradlew ktlintFormat",
            env = mapOf(
                "ENDPOINT_URL" to expr { "vars.ENDPOINT_URL" }
            )
        )

        uses(
            name = "Commit format",
            action = GitAutoCommitAction(
                commitMessage = "Lint formatting"
            )
        )

    }
}

println("Output Linting CI")