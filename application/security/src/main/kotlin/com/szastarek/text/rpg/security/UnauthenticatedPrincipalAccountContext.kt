package com.szastarek.text.rpg.security

import com.szastarek.acl.AccountContext
import com.szastarek.acl.AccountId
import com.szastarek.acl.Role
import com.szastarek.acl.UnauthenticatedPrincipalRole
import com.szastarek.acl.authority.Authority

object UnauthenticatedPrincipalAccountContext : AccountContext {
    override val accountId: AccountId = AccountId("unauthenticated")
    override val customAuthorities: List<Authority> = emptyList()
    override val role: Role = UnauthenticatedPrincipalRole
}
