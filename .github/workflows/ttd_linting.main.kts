#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.3.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradleV3
import io.github.typesafegithub.workflows.actions.stefanzweifel.GitAutoCommitActionV5
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
            action = SetupJavaV4(javaVersion = "17", distribution = SetupJavaV4.Distribution.Corretto)
        )

        uses(
            name = "Setup Gradle",
            action = ActionsSetupGradleV3()
        )

        uses(
            name = "Checkout",
            action = CheckoutV4()
        )

        run(
            name = "Change permission for Act execution",
            command = "chmod +x -R *",
            condition = expr { "github.event.act" }
        )

        run(
            name = "Lint Echo",
            command = "echo ${expr { "vars.ENDPOINT_URL" }}",
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
            action = GitAutoCommitActionV5(
                commitMessage = "Lint formatting"
            )
        )

    }

}