package com.szastarek.text.rpg.account

import kotlinx.serialization.KSerializer
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.serializer.codifiedEnumSerializer

enum class AccountStatus(override val code: String) : Codified<String> {
    Staged("staged"),
    Active("active"),
    Suspended("suspended");

    object CodifiedSerializer : KSerializer<CodifiedEnum<AccountStatus, String>> by codifiedEnumSerializer()
}
