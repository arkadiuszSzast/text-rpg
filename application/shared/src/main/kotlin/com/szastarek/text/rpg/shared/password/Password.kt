package com.szastarek.text.rpg.shared.password

import com.szastarek.text.rpg.shared.validation.Validatable
import io.konform.validation.Validation
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@JvmInline
@Serializable
value class RawPassword(val value: String) : Validatable<RawPassword> {

    fun hashpw() = HashedPassword(BCrypt.hashpw(value, BCrypt.gensalt()))

    companion object {
        val validator = Validation<RawPassword> {
            RawPassword::value {
                run(defaultPasswordValidator)
            }
        }
    }

    override val validator: Validation<RawPassword>
        get() = Companion.validator

    override fun toString(): String {
        return "RawPassword(value='*masked*')"
    }
}

@JvmInline
@Serializable
value class HashedPassword(val value: String) {
    fun matches(rawPassword: RawPassword) = BCrypt.checkpw(rawPassword.value, this.value)

    override fun toString(): String {
        return "HashedPassword(value='*masked*')"
    }
}
