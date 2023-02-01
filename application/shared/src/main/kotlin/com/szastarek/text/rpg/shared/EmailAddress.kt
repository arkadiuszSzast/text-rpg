package com.szastarek.text.rpg.shared

import com.szastarek.text.rpg.shared.validation.Validatable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.pattern
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class EmailAddress private constructor(val value: String) : Validatable<EmailAddress> {

    companion object {
        fun create(address: String) = EmailAddress(address.trim())

        val validator = Validation<EmailAddress> {
            EmailAddress::value {
                pattern(EMAIL_PATTERN) hint "validation.invalid_email_format"
            }
        }
    }

    override val validator: Validation<EmailAddress>
        get() = EmailAddress.validator
}

private const val EMAIL_PATTERN = """.+@.+\..+"""
