package com.szastarek.text.rpg.account.config

import com.szastarek.text.rpg.mail.MailProperties
import com.szastarek.text.rpg.mail.MailSubject
import com.szastarek.text.rpg.mail.MailTemplateId
import com.szastarek.text.rpg.shared.EmailAddress
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MailConfigTest : DescribeSpec() {

    init {
        describe("get mail config test") {

            it("get properties") {
                // arrange && act && assert
                expectThat(MailConfig) {
                    get { activateAccount }.isEqualTo(
                        MailProperties(
                            MailTemplateId("activate-account-template-id"),
                            MailSubject("Activate Your Account"),
                            EmailAddress.create("activate-account-sender@mail.com")
                        )
                    )
                }
            }
        }
    }
}
