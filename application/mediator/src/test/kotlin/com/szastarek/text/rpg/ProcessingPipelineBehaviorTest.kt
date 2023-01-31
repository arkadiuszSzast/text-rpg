package com.szastarek.text.rpg

import com.szastarek.text.rpg.middlewares.AsyncPipelineBehaviorMiddleware
import com.szastarek.text.rpg.middlewares.PipelineBehaviorMiddleware
import com.szastarek.text.rpg.test.utils.withKoin
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import com.trendyol.kediatr.CommandBus
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult
import com.trendyol.kediatr.CommandWithResultHandler
import com.trendyol.kediatr.koin.KediatrKoin
import io.kotest.core.spec.style.DescribeSpec
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.doesNotContain

class ProcessingPipelineBehaviorTest : KoinTest, DescribeSpec() {
    init {
        describe("ProcessingPipelineBehavior") {

            it("middleware should execute before called command") {
                val invokesList = mutableListOf<String>()
                val koinModules = module {
                    single { KediatrKoin.getCommandBus() }
                    single { PongCommandHandler(invokesList) }
                    single { PipelineBehaviorMiddlewareA(invokesList) } bind PipelineBehaviorMiddleware::class
                    single { ProcessingPipelineBehavior(getAll()) }
                }

                withKoin(koinModules) {
                    // arrange

                    val commandBus = get<CommandBus>()
                    // act
                    commandBus.executeCommand(PongCommand())

                    // assert
                    expectThat(invokesList)
                        .containsExactly("middlewareA", "pong")
                }
            }

            it("async middleware should execute before called command") {
                val invokesList = mutableListOf<String>()
                val koinModules = module {
                    single { KediatrKoin.getCommandBus() }
                    single { AsyncPongCommandHandler(invokesList) }
                    single { AsyncPipelineBehaviorMiddlewareA(invokesList) } bind AsyncPipelineBehaviorMiddleware::class
                    single { AsyncProcessingPipelineBehavior(getAll()) }
                }

                withKoin(koinModules) {
                    // arrange
                    val commandBus = get<CommandBus>()

                    // act
                    commandBus.executeCommandAsync(PongCommand())

                    // assert
                    expectThat(invokesList)
                        .containsExactly("asyncMiddlewareA", "pong")
                }
            }

            it("should pick matching middlewares for async and regular handlers") {
                val invokesList = mutableListOf<String>()
                val koinModules = module {
                    single { KediatrKoin.getCommandBus() }
                    single { PongCommandHandler(invokesList) }
                    single { AsyncPongCommandHandler(invokesList) }
                    single { PipelineBehaviorMiddlewareA(invokesList) } bind PipelineBehaviorMiddleware::class
                    single { AsyncPipelineBehaviorMiddlewareA(invokesList) } bind AsyncPipelineBehaviorMiddleware::class
                    single { AsyncProcessingPipelineBehavior(getAll()) }
                    single { ProcessingPipelineBehavior(getAll()) }
                }

                withKoin(koinModules) {
                    // arrange
                    val commandBus = get<CommandBus>()
                    // act
                    commandBus.executeCommand(PongCommand())

                    // assert
                    expectThat(invokesList) {
                        containsExactly("middlewareA", "pong")
                        doesNotContain("asyncMiddlewareA")
                    }

                    // clean
                    invokesList.clear()

                    // act
                    commandBus.executeCommandAsync(PongCommand())

                    // assert
                    expectThat(invokesList) {
                        containsExactly("asyncMiddlewareA", "pong")
                        doesNotContain("middlewareA")
                    }
                }
            }

            it("works correctly with multiple middlewares") {
                val invokesList = mutableListOf<String>()
                val koinModules = module {
                    single { KediatrKoin.getCommandBus() }
                    single { PongCommandHandler(invokesList) }
                    single { PipelineBehaviorMiddlewareA(invokesList) } bind PipelineBehaviorMiddleware::class
                    single { PipelineBehaviorMiddlewareB(invokesList) } bind PipelineBehaviorMiddleware::class
                    single { ProcessingPipelineBehavior(getAll()) }
                }

                withKoin(koinModules) {
                    // arrange
                    val commandBus = get<CommandBus>()

                    // act
                    commandBus.executeCommand(PongCommand())

                    // assert
                    expectThat(invokesList)
                        .containsExactly("middlewareA", "middlewareB", "pong")
                }
            }

            it("works correctly with multiple async middlewares") {
                val invokesList = mutableListOf<String>()
                val koinModules = module {
                    single { KediatrKoin.getCommandBus() }
                    single { AsyncPongCommandHandler(invokesList) }
                    single { AsyncPipelineBehaviorMiddlewareA(invokesList) } bind AsyncPipelineBehaviorMiddleware::class
                    single { AsyncPipelineBehaviorMiddlewareB(invokesList) } bind AsyncPipelineBehaviorMiddleware::class
                    single { AsyncProcessingPipelineBehavior(getAll()) }
                }

                withKoin(koinModules) {
                    // arrange
                    val commandBus = get<CommandBus>()

                    // act
                    commandBus.executeCommandAsync(PongCommand())

                    // assert
                    expectThat(invokesList)
                        .containsExactly("asyncMiddlewareA", "asyncMiddlewareB", "pong")
                }
            }
        }
    }
}

class PipelineBehaviorMiddlewareA(private val invokesList: MutableList<String>) : PipelineBehaviorMiddleware {
    override val order: Int = 1

    override fun <TRequest, TResponse> apply(request: TRequest, act: () -> TResponse): TResponse {
        invokesList.add("middlewareA")
        return act.invoke()
    }
}

class PipelineBehaviorMiddlewareB(private val invokesList: MutableList<String>) : PipelineBehaviorMiddleware {
    override val order: Int = 2

    override fun <TRequest, TResponse> apply(request: TRequest, act: () -> TResponse): TResponse {
        invokesList.add("middlewareB")
        return act.invoke()
    }
}

class AsyncPipelineBehaviorMiddlewareA(private val invokesList: MutableList<String>) : AsyncPipelineBehaviorMiddleware {
    override val order: Int = 1

    override suspend fun <TRequest, TResponse> apply(request: TRequest, act: suspend () -> TResponse): TResponse {
        invokesList.add("asyncMiddlewareA")
        return act.invoke()
    }
}

class AsyncPipelineBehaviorMiddlewareB(private val invokesList: MutableList<String>) : AsyncPipelineBehaviorMiddleware {
    override val order: Int = 2

    override suspend fun <TRequest, TResponse> apply(request: TRequest, act: suspend () -> TResponse): TResponse {
        invokesList.add("asyncMiddlewareB")
        return act.invoke()
    }
}

class PongCommand(override val metadata: CommandMetadata? = null) : CommandWithResult<String>
class PongCommandHandler(private val invokesList: MutableList<String>) : CommandWithResultHandler<PongCommand, String> {
    override fun handle(command: PongCommand): String {
        invokesList.add("pong")
        return "pong"
    }
}

class AsyncPongCommandHandler(private val invokesList: MutableList<String>) :
    AsyncCommandWithResultHandler<PongCommand, String> {
    override suspend fun handleAsync(command: PongCommand): String {
        invokesList.add("pong")
        return "pong"
    }
}
