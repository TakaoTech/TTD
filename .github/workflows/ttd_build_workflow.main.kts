#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.1.0")
@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("docker:login-action:v3")
@file:DependsOn("docker:build-push-action:v5")
@file:DependsOn("docker:setup-buildx-action:v3")
@file:DependsOn("gradle:actions__setup-gradle:v4")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.docker.BuildPushAction
import io.github.typesafegithub.workflows.actions.docker.LoginAction
import io.github.typesafegithub.workflows.actions.docker.SetupBuildxAction
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.actions.Action
import io.github.typesafegithub.workflows.domain.actions.RegularAction
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig

val ACT by Contexts.env

val DOCKER_HUB_USERNAME = "vars.DOCKER_HUB_USERNAME"
val DOCKER_HUB_TOKEN by Contexts.secrets

val KEYSTORE_BASE64_SECRET by Contexts.secrets
val KEYSTORE_PASSWORD by Contexts.secrets
val KEY_ALIAS by Contexts.secrets
val KEY_PASSWORD by Contexts.secrets

val keyStoreParams = listOf(KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD)

val KEYSTORE_BASE64_FILE = "TTD-App-Keystore.base64.txt"
val KEYSTORE_FILE = "TTD-App-Keystore.jks"

val GOOGLE_LOGIN_JSON_NAME by Contexts.secrets
val GOOGLE_LOGIN_JSON_BASE64 by Contexts.secrets

val FIREBASE_DISTRIBUTION_BASE64 by Contexts.secrets
val FIREBASE_JSON_BASE64 by Contexts.secrets

workflow(
    name = "Build workflow",
    on = listOf(
        Push(
            branches = listOf("test")
        )
    ),
    sourceFile = __FILE__,
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled
) {
    job(
        id = "build",
        runsOn = UbuntuLatest,
    ) {
        uses(
            name = "Setup Java",
            action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Corretto)
        )

        uses(
            name = "Set up Docker Buildx",
            action = SetupBuildxAction()
        )

        uses(name = "Setup Android SDK", action = SetupAndroidSDKV3())

        uses(
            name = "Setup Gradle",
            action = ActionsSetupGradle()
        )

        uses(
            name = "Checkout",
            action = Checkout()
        )

        run(
            name = "Write Keystore on disk",
            command = "echo ${expr { KEYSTORE_BASE64_SECRET }} > $KEYSTORE_BASE64_FILE " +
                    "| base64 -di $KEYSTORE_BASE64_FILE > $KEYSTORE_FILE"
        )

        run(
            name = "Write Google login JSON to disk",
            command = "echo ${expr { GOOGLE_LOGIN_JSON_BASE64 }} | base64 -di > composeApp/src/androidMain/${expr { GOOGLE_LOGIN_JSON_NAME }}"
        )

        run(
            name = "Write Firebase App Distribution login JSON to disk",
            command = "echo ${expr { FIREBASE_DISTRIBUTION_BASE64 }} | base64 -di > firebase-distribution.json"
        )

        run(
            name = "Write Firebase app credential JSON to disk",
            command = "echo ${expr { FIREBASE_JSON_BASE64 }} | base64 -di > composeApp/src/google-services.json"
        )

        run(
            name = "Change permission for Act execution",
            command = "chmod +x -R *",
            condition = expr { "github.event.act" })

        uses(
            name = "Login to DockerHub",
            action = LoginAction(
                username = expr(DOCKER_HUB_USERNAME),
                password = expr { DOCKER_HUB_TOKEN }
            ),
        )

        uses(
            name = "Generate Image",
            action = BuildPushAction(
                platforms = listOf("linux/amd64"),
                file = "./Dockerfile",
                push = true,
                tags = listOf("samuele794/ttd:test"),
            )
        )

        run(
            name = "Build Android App",
            command = "./gradlew composeApp:assembleStaging composeApp:appDistributionUploadStaging" + appendSecretEnvParams(
                keyStoreParams
            )
        )

//		run(name = "Generate image", command = "./gradlew server:publishImage")
    }
}

println("Output Main CI")

fun appendSecretEnvParams(keyStoreParams: List<String>): String {
    return buildString {
        append(" ")
        keyStoreParams.forEach {
            append("-P${it.replace("secrets.", "")}=\"${expr { it }}\"")
            append(" ")
        }
    }
}

class SetupAndroidSDKV3 : RegularAction<Action.Outputs>("android-actions", "setup-android", "v3") {

    override fun toYamlArguments(): LinkedHashMap<String, String> =
        linkedMapOf()

    override fun buildOutputObject(stepId: String) = Outputs(stepId)
}