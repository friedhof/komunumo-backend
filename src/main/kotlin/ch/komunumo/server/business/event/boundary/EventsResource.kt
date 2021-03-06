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
package ch.komunumo.server.business.event.boundary

import ch.komunumo.server.business.authorizeAdmin
import ch.komunumo.server.business.configuration.control.ConfigurationService
import ch.komunumo.server.business.event.control.EventService
import ch.komunumo.server.business.event.entity.Event
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.header
import org.jetbrains.ktor.response.respond

object EventsResource {

    private val baseURL: String = ConfigurationService.getServerBaseURL()

    suspend fun handleGet(call: ApplicationCall) {
        call.respond(EventService.readAll())
    }

    suspend fun handlePost(call: ApplicationCall) {
        authorizeAdmin(call)
        val event = call.receive<Event>()
        val id = EventService.create(event)
        call.response.header("Location", "${baseURL}/api/events/${id}")
        call.respond(HttpStatusCode.Created)
    }

}
