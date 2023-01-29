package com.szastarek.text.rpg.transactionally

import com.mongodb.TransactionOptions
import com.mongodb.reactivestreams.client.ClientSession
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.abortTransactionAndAwait
import org.litote.kmongo.coroutine.commitTransactionAndAwait

class TransactionallyImpl(private val mongoClient: CoroutineClient) : Transactionally {
    override suspend fun <R> call(block: suspend (ClientSession) -> R): R =
        mongoClient.startSession().call(block)

    override suspend fun <R> call(txOptions: TransactionOptions, block: suspend (ClientSession) -> R): R =
        mongoClient.startSession().call(txOptions, block)
}

suspend fun <R> ClientSession.call(block: suspend (ClientSession) -> R): R {
    this.use { session ->
        session.startTransaction()
        return runCatching { block(this) }
            .onSuccess { session.commitTransactionAndAwait() }
            .onFailure { session.abortTransactionAndAwait() }
            .getOrThrow()
    }
}

suspend fun <R> ClientSession.call(txOptions: TransactionOptions, block: suspend (ClientSession) -> R): R {
    this.use { session ->
        session.startTransaction(txOptions)
        return runCatching { block(this) }
            .onSuccess { session.commitTransactionAndAwait() }
            .onFailure { session.abortTransactionAndAwait() }
            .getOrThrow()
    }
}
