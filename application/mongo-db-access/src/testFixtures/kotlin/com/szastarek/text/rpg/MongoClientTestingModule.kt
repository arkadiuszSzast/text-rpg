package com.szastarek.text.rpg

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.szastarek.text.rpg.migrations.MongockRunnerWrapper
import com.szastarek.text.rpg.transactionally.Transactionally
import com.szastarek.text.rpg.transactionally.TransactionallyImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val kmongoTestingModule = module {
    single(createdAtStart = true) { MongockRunnerWrapper(get()) }
    single(createdAtStart = true) { KMongo.createClient(ConnectionString("mongodb://${MongoContainer.host}:${MongoContainer.port}")) }
    single(createdAtStart = true) { get<MongoClient>().coroutine }
    single(createdAtStart = true) { TransactionallyImpl(get()) } bind Transactionally::class
    factory { get<CoroutineClient>().getDatabase("test") }
}
