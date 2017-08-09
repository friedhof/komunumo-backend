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
package ch.komunumo.server

import ch.komunumo.server.authorization.Authorization
import ch.komunumo.server.event.Event
import ch.komunumo.server.event.EventService
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.header
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.route

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(GsonSupport) {
            setPrettyPrinting()
        }
        install(Routing) {
            route("api") {
                route("events") {
                    get {
                        call.respond(EventService.getAllEvents())
                    }
                    post {
                        val event = call.receive<Event>()
                        val id = EventService.addEvent(event)
                        call.response.header("Location", "/api/events/$id")
                        call.response.status(HttpStatusCode.Created)
                        call.respond("")
                    }
                    route("{id}") {
                        get {
                            val id = call.parameters["id"]
                            if (id == null) {
                                call.response.status(HttpStatusCode.BadRequest)
                                call.respond("")
                            } else {
                                val event = EventService.getEventById(id)
                                if (event == null) {
                                    call.response.status(HttpStatusCode.NotFound)
                                    call.respond("")
                                } else {
                                    call.respond(event)
                                }
                            }
                        }
                    }
                }
            }
        }
        intercept(ApplicationCallPipeline.Call) {
            Authorization.checkAuthorization(call)
        }
    }.start(wait = true)
}
