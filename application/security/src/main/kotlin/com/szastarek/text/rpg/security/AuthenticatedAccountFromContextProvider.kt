package com.szastarek.text.rpg.security

import com.szastarek.acl.AccountContext
import com.szastarek.acl.AuthenticatedAccountContext
import com.szastarek.acl.AuthenticatedAccountProvider
import com.szastarek.acl.InjectedAuthorityContext
import com.szastarek.acl.RegularRole
import com.szastarek.acl.SuperUserRole
import com.szastarek.acl.authority.Authority
import kotlin.coroutines.coroutineContext

class AuthenticatedAccountFromContextProvider : AuthenticatedAccountProvider{
    override suspend fun currentPrincipal(): AccountContext {
        val authenticatedAccountContext = coroutineContext[AuthenticatedAccountContext]
        return authenticatedAccountContext?.accountContext ?: throw NotAuthenticatedException()
    }

    override suspend fun getCustomAuthorities(): List<Authority> {
        val currentPrincipal = currentPrincipal()
        return currentPrincipal.customAuthorities
    }

    override suspend fun getRoleAuthorities(): List<Authority> {
        return when(val currentPrincipalRole = currentPrincipal().role) {
            is SuperUserRole -> emptyList()
            is RegularRole -> currentPrincipalRole.authorities
        }
    }

    override suspend fun getInjectedAuthorities(): List<Authority> {
        val injectedAuthorityContext = coroutineContext[InjectedAuthorityContext]
        return injectedAuthorityContext?.authorities ?: emptyList()
    }
}
