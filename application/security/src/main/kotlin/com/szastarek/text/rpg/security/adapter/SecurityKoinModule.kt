package com.szastarek.text.rpg.security.adapter

import com.szastarek.acl.AuthenticatedAccountProvider
import com.szastarek.acl.authority.AuthorizedAccountAbilityProvider
import com.szastarek.acl.authority.DefaultAuthorizedAccountAbilityProvider
import com.szastarek.text.rpg.security.AuthenticatedAccountFromContextProvider
import com.szastarek.text.rpg.security.RoleProvider
import com.szastarek.text.rpg.security.StaticRolesProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val securityKoinModule = module {
    single { StaticRolesProvider() } bind RoleProvider::class
    single { AuthenticatedAccountFromContextProvider() } bind AuthenticatedAccountProvider::class
    single { DefaultAuthorizedAccountAbilityProvider(get()) } bind AuthorizedAccountAbilityProvider::class
}
