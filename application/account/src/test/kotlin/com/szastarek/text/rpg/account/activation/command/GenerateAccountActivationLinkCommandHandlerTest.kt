package com.szastarek.text.rpg.account.activation.command

import com.szastarek.acl.CanDoAnythingAuthorizedAccountAbilityProvider
import com.szastarek.acl.DenyAllAuthorizedAccountAbilityProvider
import com.szastarek.acl.authority.AuthorityCheckException
import com.szastarek.text.rpg.account.config.JwtConfig
import com.szastarek.text.rpg.shared.config.ApplicationConfig
import io.kotest.core.spec.style.DescribeSpec
import org.litote.kmongo.newId
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isNotNull
import strikt.assertions.startsWith

class GenerateAccountActivationLinkCommandHandlerTest : DescribeSpec() {

    private val allowAllAcl = CanDoAnythingAuthorizedAccountAbilityProvider()
    private val denyAllAcl = DenyAllAuthorizedAccountAbilityProvider()

    private val allowingHandler = GenerateAccountActivationLinkCommandHandler(ApplicationConfig, JwtConfig, allowAllAcl)
    private val denyingHandler = GenerateAccountActivationLinkCommandHandler(ApplicationConfig, JwtConfig, denyAllAcl)
    init {

        describe("generate account activation link command handler") {

            it("should generate") {
                //arrange
                val command = GenerateAccountActivationLinkCommand(newId())

                //act
                val result = allowingHandler.handleAsync(command)

                //assert
                expectThat(result) {
                    and { get { url.toString() }.startsWith("${ApplicationConfig.webClientAppUrl}account/activate") }
                    and {
                        get { url.parameters["token"] }.isNotNull()
                    }

                }
            }

            it("should not generate account activation link when no permissions") {
                //arrange
                val command = GenerateAccountActivationLinkCommand(newId())

                //act && assert
                expectThrows<AuthorityCheckException> {
                    denyingHandler.handleAsync(command)
                }
            }
        }

    }
}
