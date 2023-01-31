package com.szastarek.text.rpg

import kotlinx.serialization.KSerializer
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.serializer.codifiedEnumSerializer

enum class KediatrRequestType(override val code: String) : Codified<String> {
    Command("command"),
    CommandWithResult("command-with-result"),
    Query("query"),
    Notification("notification");

    object CodifiedSerializer : KSerializer<CodifiedEnum<KediatrRequestType, String>> by codifiedEnumSerializer()
}
