package com.szastarek.text.rpg.account.config

import com.szastarek.text.rpg.mail.MailProperties
import com.szastarek.text.rpg.mail.MailSubject
import com.szastarek.text.rpg.mail.MailTemplateId
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.config.ConfigKey
import com.szastarek.text.rpg.shared.config.getProperty
import com.szastarek.text.rpg.shared.validation.validateEagerly

internal object MailConfig {
    val activateAccount by lazy {
        val templateId = getProperty(Keys.activateAccount + Keys.templateId)
        val sender = getProperty(Keys.activateAccount + Keys.sender)
        val subject = getProperty(Keys.activateAccount + Keys.subject)

        MailProperties(
            MailTemplateId(templateId),
            MailSubject(subject),
            EmailAddress.create(sender).validateEagerly()
        )
    }

    private object Keys {
        val templateId = ConfigKey("templateId")
        val sender = ConfigKey("sender")
        val subject = ConfigKey("subject")
        val activateAccount = ConfigKey("mail.activateAccount")
    }
}
