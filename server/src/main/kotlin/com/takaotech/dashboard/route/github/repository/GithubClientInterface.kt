package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import org.kohsuke.github.GitHub
import org.koin.core.annotation.Factory
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.kohsuke.github.GHRepository as GHRepositoryExternal

interface GithubClientInterface {

	suspend fun getAllStarsRemote(): List<GHRepositoryDao>

	suspend fun getLanguagesByRepository(repositoryId: Long): Map<String, Long>
}

@Factory
class GithubClientImpl(
	private val logger: Logger,
	private val githubClient: GitHub
) : GithubClientInterface {

	override suspend fun getAllStarsRemote(): List<GHRepositoryDao> = coroutineScope {
		val downloadedRepository = getAllStarsRemoteInternal()

		val mapJobs = mutableListOf<Deferred<List<GHRepositoryDao>>>()

		try {
			downloadedRepository.let {
				//TODO Make split size a constant
				if (it.size < 4) {
					listOf(it)
				} else {
					it.chunked(it.size / 4)
				}
			}.forEach {
				mapJobs.add(
					async(Dispatchers.Default) {
						try {
							it.mapNotNull { repository ->
								logger.info("Processing repository ID=${repository.id} Name=${repository.name} ")
								repository.convertToGHRepositoryWithDefaults()
							}
						} catch (ex: Exception) {
							logger.error(ex)
							throw ex
							/* TODO Manage exception
							 * 2024-03-01 08:20:25.479 [DefaultDispatcher-worker-4] ERROR ktor.application - Server returned HTTP response code: -1, message: 'null' for URL: https://api.github.com/repos/binwiederhier/ntfy/languages
							 * org.kohsuke.github.HttpException: Server returned HTTP response code: -1, message: 'null' for URL: https://api.github.com/repos/binwiederhier/ntfy/languages
							 * 	at org.kohsuke.github.GitHubClient.interpretApiError(GitHubClient.java:669)
							 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:480)
							 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:427)
							 * 	at org.kohsuke.github.Requester.fetch(Requester.java:85)
							 * 	at org.kohsuke.github.GHRepository.listLanguages(GHRepository.java:616)
							 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2$2$1.invokeSuspend(GithubRepository.kt:55)
							 * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
							 * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
							 * 	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:584)
							 * 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:793)
							 * 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:697)
							 * 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:684)
							 * Caused by: java.io.IOException: Received RST_STREAM: Stream not processed
							 * 	at java.net.http/jdk.internal.net.http.HttpClientImpl.send(HttpClientImpl.java:586)
							 * 	at java.net.http/jdk.internal.net.http.HttpClientFacade.send(HttpClientFacade.java:123)
							 * 	at org.kohsuke.github.extras.HttpClientGitHubConnector.send(HttpClientGitHubConnector.java:72)
							 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:461)
							 * 	... 10 common frames omitted
							 * Caused by: java.io.IOException: Received RST_STREAM: Stream not processed
							 * 	at java.net.http/jdk.internal.net.http.Stream.handleReset(Stream.java:534)
							 * 	at java.net.http/jdk.internal.net.http.Stream.incoming_reset(Stream.java:505)
							 * 	at java.net.http/jdk.internal.net.http.Stream.otherFrame(Stream.java:452)
							 * 	at java.net.http/jdk.internal.net.http.Stream.incoming(Stream.java:445)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection.processFrame(Http2Connection.java:815)
							 * 	at java.net.http/jdk.internal.net.http.frame.FramesDecoder.decode(FramesDecoder.java:155)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection$FramesController.processReceivedData(Http2Connection.java:232)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection.asyncReceive(Http2Connection.java:677)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection$Http2TubeSubscriber.processQueue(Http2Connection.java:1313)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$LockingRestartableTask.run(SequentialScheduler.java:205)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$CompleteRestartableTask.run(SequentialScheduler.java:149)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$SchedulableTask.run(SequentialScheduler.java:230)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler.runOrSchedule(SequentialScheduler.java:303)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler.runOrSchedule(SequentialScheduler.java:256)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection$Http2TubeSubscriber.runOrSchedule(Http2Connection.java:1331)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection$Http2TubeSubscriber.onNext(Http2Connection.java:1357)
							 * 	at java.net.http/jdk.internal.net.http.Http2Connection$Http2TubeSubscriber.onNext(Http2Connection.java:1291)
							 * 	at java.net.http/jdk.internal.net.http.common.SSLTube$DelegateWrapper.onNext(SSLTube.java:210)
							 * 	at java.net.http/jdk.internal.net.http.common.SSLTube$SSLSubscriberWrapper.onNext(SSLTube.java:492)
							 * 	at java.net.http/jdk.internal.net.http.common.SSLTube$SSLSubscriberWrapper.onNext(SSLTube.java:295)
							 * 	at java.net.http/jdk.internal.net.http.common.SubscriberWrapper$DownstreamPusher.run1(SubscriberWrapper.java:316)
							 * 	at java.net.http/jdk.internal.net.http.common.SubscriberWrapper$DownstreamPusher.run(SubscriberWrapper.java:259)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$LockingRestartableTask.run(SequentialScheduler.java:205)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$CompleteRestartableTask.run(SequentialScheduler.java:149)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$SchedulableTask.run(SequentialScheduler.java:230)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler.runOrSchedule(SequentialScheduler.java:303)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler.runOrSchedule(SequentialScheduler.java:256)
							 * 	at java.net.http/jdk.internal.net.http.common.SubscriberWrapper.outgoing(SubscriberWrapper.java:232)
							 * 	at java.net.http/jdk.internal.net.http.common.SubscriberWrapper.outgoing(SubscriberWrapper.java:198)
							 * 	at java.net.http/jdk.internal.net.http.common.SSLFlowDelegate$Reader.processData(SSLFlowDelegate.java:444)
							 * 	at java.net.http/jdk.internal.net.http.common.SSLFlowDelegate$Reader$ReaderDownstreamPusher.run(SSLFlowDelegate.java:268)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$LockingRestartableTask.run(SequentialScheduler.java:205)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$CompleteRestartableTask.run(SequentialScheduler.java:149)
							 * 	at java.net.http/jdk.internal.net.http.common.SequentialScheduler$SchedulableTask.run(SequentialScheduler.java:230)
							 * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
							 * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
							 * 	at java.base/java.lang.Thread.run(Thread.java:840)
							 */
						}
					}
				)
			}
		} catch (ex: Exception) {
			logger.error("Error download getAllStars", ex)
			//TODO throw correct exception
			throw ex
		}

		mapJobs.awaitAll().flatten()
	}

	private suspend fun getAllStarsRemoteInternal(): List<GHRepositoryExternal> = suspendCoroutine {
		try {
			val stars = githubClient
				.myself
				.listStarredRepositories()
				.toList()
			it.resume(stars)
		} catch (ex: Throwable) {
			/* TODO Manage exception
			 * org.kohsuke.github.HttpException: {"message":"Bad credentials","documentation_url":"https://docs.github.com/rest"}
			 * 	at org.kohsuke.github.GitHubConnectorResponseErrorHandler$1.onError(GitHubConnectorResponseErrorHandler.java:62)
			 * 	at org.kohsuke.github.GitHubClient.detectKnownErrors(GitHubClient.java:504)
			 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:464)
			 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:427)
			 * 	at org.kohsuke.github.Requester.fetch(Requester.java:85)
			 * 	at org.kohsuke.github.GitHub.setMyself(GitHub.java:583)
			 * 	at org.kohsuke.github.GitHub.getMyself(GitHub.java:577)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository.getAllStarsRemote(GithubRepository.kt:72)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository.access$getAllStarsRemote(GithubRepository.kt:15)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2.invokeSuspend(GithubRepository.kt:21)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2.invoke(GithubRepository.kt)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2.invoke(GithubRepository.kt)
			 * 	at kotlinx.coroutines.intrinsics.UndispatchedKt.startUndispatchedOrReturn(Undispatched.kt:78)
			 * 	at kotlinx.coroutines.CoroutineScopeKt.coroutineScope(CoroutineScope.kt:264)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository.getAllStars(GithubRepository.kt:20)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController$getStarsFromZeroAndStore$2.invokeSuspend(GithubController.kt:20)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController$getStarsFromZeroAndStore$2.invoke(GithubController.kt)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController$getStarsFromZeroAndStore$2.invoke(GithubController.kt)
			 * 	at kotlinx.coroutines.intrinsics.UndispatchedKt.startUndispatchedOrReturn(Undispatched.kt:78)
			 * 	at kotlinx.coroutines.CoroutineScopeKt.coroutineScope(CoroutineScope.kt:264)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController.getStarsFromZeroAndStore(GithubController.kt:18)
			 * 	at com.takaotech.dashboard.route.github.GithubRouteKt$githubRoute$1$2.invokeSuspend(GithubRouter.kt:28)
			 * 	at com.takaotech.dashboard.route.github.GithubRouteKt$githubRoute$1$2.invoke(GithubRouter.kt)
			 * 	at com.takaotech.dashboard.route.github.GithubRouteKt$githubRoute$1$2.invoke(GithubRouter.kt
			 */

			logger.error("Error getAllStartsRemote", ex)
			it.resumeWithException(ex)
		}
	}

	override suspend fun getLanguagesByRepository(repositoryId: Long): Map<String, Long> =
		suspendCoroutine<Map<String, Long>> {
			try {
				val listLanguages = githubClient
					.getRepositoryById(repositoryId)
					.listLanguages()
				it.resume(listLanguages)
			} catch (ex: IOException) {
				logger.error("Error getLanguagesByRepository", ex)
				it.resumeWithException(ex)
			}
		}
}