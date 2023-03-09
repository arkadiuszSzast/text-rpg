package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.activation.AccountActivationError
import com.szastarek.text.rpg.account.activation.event.AccountActivatedEvent
import com.szastarek.text.rpg.account.activation.event.AccountActivationFailureEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInFailureEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInSuccessEvent
import com.szastarek.text.rpg.shared.password.RawPassword
import com.szastarek.text.rpg.test.utils.faker
import io.kotest.core.spec.style.DescribeSpec
import pl.brightinventions.codified.enums.codifiedEnum
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class AccountAggregateTest : DescribeSpec() {

    init {
        describe("AccountAggregate") {

            describe("log in") {

                it("should log in") {
                    //arrange
                    val rawPassword = faker.accountModule.rawPassword()
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Active
                        password = rawPassword.hashpw()
                    }

                    //act
                    val result = account.logIn(rawPassword)

                    //assert
                    expectThat(result) {
                        isA<AccountLoggedInSuccessEvent>()
                            .get { accountId }.isEqualTo(account.id)
                    }
                }

                it("should not log in when account is not active") {
                    //arrange
                    val rawPassword = faker.accountModule.rawPassword()
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Staged
                        password = rawPassword.hashpw()
                    }

                    //act
                    val result = account.logIn(rawPassword)

                    //assert
                    expectThat(result) {
                        isA<AccountLoggedInFailureEvent>()
                            .and { get { accountId }.isEqualTo(account.id) }
                            .and { get { reason }.isEqualTo(LogInFailureError.AccountNotActivated.codifiedEnum()) }
                    }
                }

                it("should not log in when account is suspended") {
                    //arrange
                    val rawPassword = faker.accountModule.rawPassword()
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Suspended
                        password = rawPassword.hashpw()
                    }

                    //act
                    val result = account.logIn(rawPassword)

                    //assert
                    expectThat(result) {
                        isA<AccountLoggedInFailureEvent>()
                            .and { get { accountId }.isEqualTo(account.id) }
                            .and { get { reason }.isEqualTo(LogInFailureError.AccountSuspended.codifiedEnum()) }
                    }
                }

                it("should not log in when password is incorrect") {
                    //arrange
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Active
                    }

                    //act
                    val result = account.logIn(RawPassword("incorrect"))

                    //assert
                    expectThat(result) {
                        isA<AccountLoggedInFailureEvent>()
                            .and { get { accountId }.isEqualTo(account.id) }
                            .and { get { reason }.isEqualTo(LogInFailureError.InvalidPassword.codifiedEnum()) }
                    }
                }
            }

            describe("activate") {

                it("should activate account") {
                    //arrange
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Staged
                    }

                    //act
                    val result = account.activate()

                    //assert
                    expectThat(result) {
                        isA<AccountActivatedEvent>()
                            .get { accountId }.isEqualTo(account.id)
                    }
                }

                it("should not activate account when account is active") {
                    //arrange
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Active
                    }

                    //act
                    val result = account.activate()

                    //assert
                    expectThat(result) {
                        isA<AccountActivationFailureEvent>()
                            .and { get { accountId }.isEqualTo(account.id) }
                            .and { get { reason }.isEqualTo(AccountActivationError.AccountActive.codifiedEnum()) }
                    }
                }

                it("should not activate account when account is suspended") {
                    //arrange
                    val account = faker.accountModule.accountAggregate {
                        status = AccountStatus.Suspended
                    }

                    //act
                    val result = account.activate()

                    //assert
                    expectThat(result) {
                        isA<AccountActivationFailureEvent>()
                            .and { get { accountId }.isEqualTo(account.id) }
                            .and { get { reason }.isEqualTo(AccountActivationError.AccountSuspended.codifiedEnum()) }
                    }
                }
            }
        }
    }
}
