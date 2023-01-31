package com.szastarek.text.rpg.middlewares

interface AsyncPipelineBehaviorMiddleware {
    val order: Int

    suspend fun <TRequest, TResponse> apply(request: TRequest, act: suspend () -> TResponse): TResponse
}
