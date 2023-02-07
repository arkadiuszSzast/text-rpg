package com.szastarek.text.rpg.account.adapter

import com.szastarek.text.rpg.account.AccountProjectionRepository
import com.szastarek.text.rpg.account.commands.CreateAccountCommandHandler
import com.szastarek.text.rpg.account.subscribers.AccountProjectionUpdater
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val koinAccountModule = module {
    single { AccountProjectionMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountProjectionRepository::class
    single { CreateAccountCommandHandler(get(), get()) }
    single { AccountProjectionUpdater(get()) }
}
