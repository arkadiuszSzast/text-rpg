package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.commands.CreateAccountCommand
import com.szastarek.text.rpg.account.commands.LoginAccountCommand
import com.szastarek.text.rpg.account.commands.LoginAccountCommandSucceed
import com.szastarek.text.rpg.account.requests.CreateAccountRequest
import com.szastarek.text.rpg.account.requests.LoginAccountRequest
import com.szastarek.text.rpg.account.responses.CreateAccountResponse
import com.szastarek.text.rpg.account.responses.LoginAccountResponse
import com.szastarek.text.rpg.shared.validation.validateEagerly
import com.trendyol.kediatr.CommandBus
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureAccountRouting() {

    val commandBus: CommandBus by inject()

    routing {
        post(AccountApi.v1) {
            val request = call.receive<CreateAccountRequest>().validateEagerly()
            val result = commandBus.executeCommandAsync(
                CreateAccountCommand(
                    request.email,
                    request.password,
                    request.timeZoneId
                )
            )

            call.respond(HttpStatusCode.Created, CreateAccountResponse(result.accountId))
        }

        post("${AccountApi.v1}/login") {
            val request = call.receive<LoginAccountRequest>().validateEagerly()
            val result = commandBus.executeCommandAsync(LoginAccountCommand(request.email, request.password))

            when (result) {
                is LoginAccountCommandSucceed -> call.respond(HttpStatusCode.OK, LoginAccountResponse(result.token))
                else -> call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
