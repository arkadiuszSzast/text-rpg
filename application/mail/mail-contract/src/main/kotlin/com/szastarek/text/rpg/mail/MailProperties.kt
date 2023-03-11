package com.szastarek.text.rpg.mail

import com.szastarek.text.rpg.shared.EmailAddress

data class MailProperties(val templateId: MailTemplateId, val subject: MailSubject, val sender: EmailAddress)
