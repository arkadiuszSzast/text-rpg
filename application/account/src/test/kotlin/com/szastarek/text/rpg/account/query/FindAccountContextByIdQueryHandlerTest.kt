package com.szastarek.text.rpg.account.query

import com.szastarek.acl.AccountId
import com.szastarek.acl.Feature
import com.szastarek.acl.authority.authorities
import com.szastarek.text.rpg.account.InMemoryAccountAggregateRepository
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.security.StaticRolesProvider
import com.szastarek.text.rpg.test.utils.faker
import io.kotest.core.spec.style.DescribeSpec
import org.litote.kmongo.newId
import strikt.api.expectThat
import strikt.arrow.isNone
import strikt.arrow.isSome
import strikt.assertions.isEqualTo

class FindAccountContextByIdQueryHandlerTest : DescribeSpec() {
    private val roleProvider = StaticRolesProvider()
    private val accountAggregateRepository = InMemoryAccountAggregateRepository()
    private val handler = FindAccountContextByIdQueryHandler(roleProvider, accountAggregateRepository)

    init {

        beforeEach {
            accountAggregateRepository.clear()
        }

        describe("FindAccountContextByIdQueryHandler") {

            it("should return account context") {
                //arrange
                val account = faker.accountModule.accountAggregate {
                    customAuthorities = authorities { featureAccess(Feature("test")) }
                }.also { accountAggregateRepository.save(it) }
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
