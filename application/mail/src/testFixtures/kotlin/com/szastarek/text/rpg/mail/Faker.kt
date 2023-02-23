package com.szastarek.text.rpg.mail

import com.szastarek.text.rpg.mail.command.SendMailCommand
import com.szastarek.text.rpg.shared.EmailAddress
import io.github.serpro69.kfaker.Faker
import org.litote.kmongo.Id
import org.litote.kmongo.newId

val Faker.mailModule: MailModule
    get() = MailModule(this)

class MailModule(private val faker: Faker) {
    fun mailSubject() = MailSubject(faker.book.title())
    fun mailTemplateId() = MailTemplateId(faker.random.nextUUID())
    fun mailAggregate(
        id: Id<Mail> = newId(),
        subject: MailSubject = mailSubject(),
        from: EmailAddress = EmailAddress.create("${faker.animal.name()}@mail.com"),
        to: EmailAddress = EmailAddress.create("${faker.familyGuy.character()}@mail.com"),
        mailTemplate: MailTemplateId = mailTemplateId(),
        mailVariables: MailVariables = MailVariables(emptyMap())
    ) = MailAggregate(
        id,
        subject,
        from,
        to,
        mailTemplate,
        mailVariables
    )

    fun sendMailCommand(
        subject: MailSubject = mailSubject(),
        from: EmailAddress = EmailAddress.create("${faker.animal.name()}@mail.com"),
        to: EmailAddress = EmailAddress.create("${faker.familyGuy.character()}@mail.com"),
        mailTemplate: MailTemplateId = mailTemplateId(),
        mailVariables: MailVariables = MailVariables(emptyMap())
    ) = SendMailCommand(subject, from, to, mailTemplate, mailVariables)

}
