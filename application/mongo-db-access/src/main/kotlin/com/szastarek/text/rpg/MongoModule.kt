package com.szastarek.text.rpg

import com.mongodb.ConnectionString
import com.szastarek.text.rpg.config.DatabaseConfig
import com.szastarek.text.rpg.transactionally.Transactionally
import com.szastarek.text.rpg.transactionally.TransactionallyImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mongoModule = module {
    single { KMongo.createClient(ConnectionString(DatabaseConfig.connectionString)).coroutine }
    factory { get<CoroutineClient>().getDatabase(DatabaseConfig.name) }
    single { TransactionallyImpl(get()) } bind Transactionally::class
}
