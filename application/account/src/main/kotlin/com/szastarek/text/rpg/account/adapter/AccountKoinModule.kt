package com.szastarek.text.rpg.account.adapter

import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.activation.command.AccountActivateCommandHandler
import com.szastarek.text.rpg.account.activation.command.GenerateAccountActivationLinkCommandHandler
import com.szastarek.text.rpg.account.command.CreateAccountCommandHandler
import com.szastarek.text.rpg.account.command.LoginAccountHandler
import com.szastarek.text.rpg.account.config.JwtConfig
import com.szastarek.text.rpg.account.query.FindAccountContextByIdQueryHandler
import com.szastarek.text.rpg.account.subscriber.AccountAggregateUpdater
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import com.szastarek.text.rpg.shared.config.ApplicationConfig
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val accountKoinModule = module {
    single { AccountAggregateMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountAggregateRepository::class
    single { CreateAccountCommandHandler(get(), get()) }
    single { FindAccountContextByIdQueryHandler(get(), get()) }
    single { LoginAccountHandler(JwtAuthConfig, get(), get()) }
    single { AccountAggregateUpdater(get()) }
    single { GenerateAccountActivationLinkCommandHandler(ApplicationConfig, JwtConfig, get()) }
    single { AccountActivateCommandHandler(JwtConfig, get(), get()) }
}

