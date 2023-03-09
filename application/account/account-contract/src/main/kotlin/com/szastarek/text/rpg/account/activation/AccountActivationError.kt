package com.szastarek.text.rpg.account.activation

import kotlinx.serialization.KSerializer
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.serializer.codifiedEnumSerializer

enum class AccountActivationError(override val code: String) : Codified<String> {
    AccountActive("account_active"),
    AccountStatusUnknown("account_status_unknown"),
    AccountSuspended("account_suspended"),
    TokenExpired("token_expired"),
    TokenInvalid("token_invalid"),
    AccountNotFound("account_not_found");

    object CodifiedSerializer : KSerializer<CodifiedEnum<AccountActivationError, String>> by codifiedEnumSerializer()
}
