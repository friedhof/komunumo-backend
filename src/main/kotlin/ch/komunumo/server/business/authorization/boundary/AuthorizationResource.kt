/*
 * Komunumo – Open Source Community Manager
 * Copyright (C) 2017 Java User Group Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.komunumo.server.business.authorization.boundary

import ch.komunumo.server.business.authorization.control.AuthorizationService
import ch.komunumo.server.business.authorization.entity.Challenge
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.header
import org.jetbrains.ktor.response.respond

object AuthorizationResource {

    suspend fun handleGet(call: ApplicationCall) {
        val email = call.parameters["email"]
        if (email.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest)
        }
        try {
            AuthorizationService.sendOnetimeLoginCode(email!!)
            call.respond(HttpStatusCode.OK)
        } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    suspend fun handlePost(call: ApplicationCall) {
        val authorization = call.receive<Challenge>()
        try {
            val token = AuthorizationService.getTokenForChallenge(authorization)
            call.response.header("Authorization", "bearer ${token}")
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

}
