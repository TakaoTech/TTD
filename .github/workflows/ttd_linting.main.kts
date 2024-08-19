#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.1.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.stefanzweifel.GitAutoCommitActionV5
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

val ENDPOINT_URL by Contexts.env

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
            name = "Checkout",
            action = CheckoutV4()
        )

        run(
            name = "Lint Fix",
            command = "./gradlew ktlintFormat",
            env = mapOf(
                "ENDPOINT_URL" to ENDPOINT_URL
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