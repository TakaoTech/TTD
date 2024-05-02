#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.15.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradleV3
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile

workflow(
	name = "Server build workflow",
	on = listOf(Push(branches = listOf("test"))),
	sourceFile = __FILE__.toPath(),
) {
	job(id = "build_server", runsOn = UbuntuLatest) {
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

		//'${{ github.event.act }}'

		run(name ="Change permission for Act execution", command = "chmod +x -R *", condition = "\${{ env.ACT }}")

		run(name = "Generate image", command = "./gradlew server:publishImage")
	}
}.writeToFile()