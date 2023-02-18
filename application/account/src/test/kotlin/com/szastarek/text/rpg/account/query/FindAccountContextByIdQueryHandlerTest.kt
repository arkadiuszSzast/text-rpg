package com.szastarek.text.rpg.account.query

import com.szastarek.acl.AccountId
import com.szastarek.acl.Feature
import com.szastarek.acl.authority.authorities
import com.szastarek.text.rpg.DatabaseTest
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.adapter.AccountAggregateMongoRepository
import com.szastarek.text.rpg.account.createAccountAggregate
import com.szastarek.text.rpg.security.RoleProvider
import com.szastarek.text.rpg.security.StaticRolesProvider
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.newId
import strikt.api.expectThat
import strikt.arrow.isNone
import strikt.arrow.isSome
import strikt.assertions.isEqualTo

private val testingModules = module {
    single { StaticRolesProvider() } bind RoleProvider::class
    single { AccountAggregateMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountAggregateRepository::class
    single { FindAccountContextByIdQueryHandler(get(), get()) }
}

class FindAccountContextByIdQueryHandlerTest : DatabaseTest(testingModules) {
    private val handler = get<FindAccountContextByIdQueryHandler>()
    private val roleProvider = get<RoleProvider>()

    init {
        describe("FindAccountContextByIdQueryHandler") {

            it("should return account context") {
                //arrange
                val account = createAccountAggregate {
                    customAuthorities = authorities { featureAccess(Feature("test")) }
                }
                val query = FindAccountContextByIdQuery(account.id)

                //act
                val result = handler.handleAsync(query)

                //assert
                expectThat(result).isSome().get { value }
                    .and { get { accountId }.isEqualTo(AccountId(account.id.toString())) }
                    .and { get { customAuthorities }.isEqualTo(account.customAuthorities) }
                    .and { get { role }.isEqualTo(roleProvider.getByName(account.roleName)) }
            }

            it("should return empty if account not found") {
                //given
                val query = FindAccountContextByIdQuery(newId())

                //when
                val result = handler.handleAsync(query)

                //then
                expectThat(result).isNone()
            }
        }

    }
}
