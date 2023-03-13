package com.szastarek.text.rpg.account.query

import arrow.core.Option
import com.szastarek.acl.AccountContext
import com.szastarek.acl.AccountId
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.security.RoleProvider
import com.trendyol.kediatr.QueryHandler
import mu.KotlinLogging

class FindAccountContextByIdQueryHandler(
    private val roleProvider: RoleProvider,
    private val accountAggregateRepository: AccountAggregateRepository
) : QueryHandler<FindAccountContextByIdQuery, Option<AccountContext>> {
    private val logger = KotlinLogging.logger {}
    override suspend fun handle(query: FindAccountContextByIdQuery): Option<AccountContext> {
        return accountAggregateRepository.findById(query.id).map { accountAggregate ->
                object: AccountContext {
                    override val accountId = AccountId(accountAggregate.id.toString())
                    override val customAuthorities = accountAggregate.customAuthorities
                    override val role = roleProvider.getByName(accountAggregate.roleName)
                }
        }
            .tapNone { logger.warn { "Account with id [${query.id}] not found" } }
    }
}
