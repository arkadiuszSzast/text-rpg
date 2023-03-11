package com.szastarek.text.rpg.account.activation.mail

import com.szastarek.text.rpg.mail.MailVariables

data class ActivateAccountMailVariables(
    val activationUrl: String
) {
    fun toMailVariables() = MailVariables(
        mapOf("activate_account_url" to activationUrl)
    )
}
