package com.szastarek.text.rpg.mail.adapter

import com.sendgrid.SendGrid
import com.szastarek.text.rpg.mail.MailSender
import com.szastarek.text.rpg.mail.SendGridMailSender
import com.szastarek.text.rpg.mail.config.SendGridConfig
import org.koin.dsl.bind
import org.koin.dsl.module

val mailKoinModule = module {
    single { SendGrid(SendGridConfig.apiKey) }
    single { SendGridMailSender(get()) } bind MailSender::class
    single {}
}
