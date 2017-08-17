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
package ch.komunumo.server.business

import ch.komunumo.server.business.authorization.control.AuthorizationService
import ch.komunumo.server.business.configuration.control.ConfigurationService
import ch.komunumo.server.business.user.entity.UserRole
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.response.respond
import java.util.UUID

fun generateNewUniqueId(existingIds: Collection<String>) : String {
    var id: String
    do {
        id = UUID.randomUUID().toString()
    } while (existingIds.contains(id))
    return id;
}

suspend fun authorizeAdmin(call: ApplicationCall) {
    val user = call.attributes.getOrNull(AuthorizationService.UserAttribute)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
    } else if (user.role != UserRole.ADMIN) {
        call.respond(HttpStatusCode.Forbidden)
    }
}

suspend fun authorizeMember(call: ApplicationCall, userId: String? = null) {
    val user = call.attributes.getOrNull(AuthorizationService.UserAttribute)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized)
    } else if (user.role != UserRole.MEMBER && user.role != UserRole.ADMIN) {
        call.respond(HttpStatusCode.Forbidden)
    } else if(user.role == UserRole.MEMBER && user.id != null && user.id != userId) {
        call.respond(HttpStatusCode.Forbidden)
    }
}

fun sendEmail(email: String, subject: String, text: String) {
    val mail = SimpleEmail()
    mail.setHostName(ConfigurationService.getSMTPServer())
    mail.setSmtpPort(ConfigurationService.getSMTPPort())
    mail.setAuthenticator(DefaultAuthenticator(ConfigurationService.getSMTPUser(), ConfigurationService.getSMTPPassword()))
    mail.setSSLOnConnect(ConfigurationService.getSMTPSSL())
    mail.setFrom(ConfigurationService.getSMTPFrom())
    mail.setSubject(subject)
    mail.setMsg(text)
    mail.addTo(email)
    mail.send()
}
