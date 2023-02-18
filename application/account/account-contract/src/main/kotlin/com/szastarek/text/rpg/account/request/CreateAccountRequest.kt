package com.szastarek.text.rpg.account.request

import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.password.RawPassword
import com.szastarek.text.rpg.shared.validation.Validatable
import io.konform.validation.Validation
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest constructor(
    val email: EmailAddress,
    val password: RawPassword,
    val timeZoneId: TimeZone
) : Validatable<CreateAccountRequest> {

    companion object {
        val validator = Validation<CreateAccountRequest> {
            CreateAccountRequest::password {
                run(RawPassword.validator)
            }
            CreateAccountRequest::email {
                run(EmailAddress.validator)
            }
        }
    }

    override val validator: Validation<CreateAccountRequest>
        get() = CreateAccountRequest.validator
}
