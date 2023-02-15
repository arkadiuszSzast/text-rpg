package com.szastarek.text.rpg.account.requests

import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.password.RawPassword
import com.szastarek.text.rpg.shared.validation.Validatable
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class LoginAccountRequest(val email: EmailAddress, val password: RawPassword) :
    Validatable<LoginAccountRequest> {

    companion object {
        val validator = Validation {
            LoginAccountRequest::email {
                run(EmailAddress.validator)
            }
        }
    }

    override val validator: Validation<LoginAccountRequest>
        get() = LoginAccountRequest.validator
}
