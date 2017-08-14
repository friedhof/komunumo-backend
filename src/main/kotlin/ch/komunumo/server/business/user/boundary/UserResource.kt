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
package ch.komunumo.server.business.user.boundary

import ch.komunumo.server.business.authorizeMember
import ch.komunumo.server.business.user.control.UserService
import ch.komunumo.server.business.user.entity.User
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import java.util.ConcurrentModificationException

object UserResource {

    suspend fun handleGet(call: ApplicationCall) {
        val id = call.parameters["id"]!!
        authorizeMember(call, id)
        try {
            call.respond(UserService.readById(id))
        } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    suspend fun handlePut(call: ApplicationCall) {
        val id = call.parameters["id"]!!
        authorizeMember(call, id)
        val user = call.receive<User>().copy(id = id)
        try {
            call.respond(UserService.update(user))
        } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound)
        } catch (e: ConcurrentModificationException) {
            call.respond(HttpStatusCode.Conflict)
        }
    }

}
