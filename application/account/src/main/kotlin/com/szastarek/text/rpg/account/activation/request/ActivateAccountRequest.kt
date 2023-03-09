package com.szastarek.text.rpg.account.activation.request

import com.szastarek.text.rpg.shared.jwt.JwtToken
import com.szastarek.text.rpg.shared.validation.Validatable
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class ActivateAccountRequest(val token: JwtToken) : Validatable<ActivateAccountRequest> {

    companion object {
        val validator = Validation<ActivateAccountRequest> {
            ActivateAccountRequest::token {
                run(JwtToken.validator)
            }
        }
    }

    override val validator: Validation<ActivateAccountRequest>
        get() = ActivateAccountRequest.validator
}
