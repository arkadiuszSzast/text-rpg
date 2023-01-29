package com.szastarek.text.rpg.transactionally

import com.mongodb.TransactionOptions
import com.mongodb.reactivestreams.client.ClientSession

interface Transactionally {
    suspend fun <R> call(block: suspend (ClientSession) -> R): R
    suspend fun <R> call(txOptions: TransactionOptions, block: suspend (ClientSession) -> R): R
}
