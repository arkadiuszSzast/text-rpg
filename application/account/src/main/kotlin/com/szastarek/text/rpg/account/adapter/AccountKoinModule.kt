package com.szastarek.text.rpg.account.adapter

import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.commands.CreateAccountCommandHandler
import com.szastarek.text.rpg.account.commands.LoginAccountHandler
import com.szastarek.text.rpg.account.query.FindAccountContextByIdQueryHandler
import com.szastarek.text.rpg.account.subscribers.AccountAggregateUpdater
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val koinAccountModule = module {
    single { AccountAggregateMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountAggregateRepository::class
    single { CreateAccountCommandHandler(get(), get()) }
    single { FindAccountContextByIdQueryHandler(get(), get()) }
    single { LoginAccountHandler(JwtAuthConfig, get(), get()) }
    single { AccountAggregateUpdater(get()) }
}

