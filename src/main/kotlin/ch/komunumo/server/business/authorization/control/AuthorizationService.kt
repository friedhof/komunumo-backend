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
package ch.komunumo.server.business.authorization.control

import ch.komunumo.server.business.sendEmail
import ch.komunumo.server.business.user.control.UserService
import ch.komunumo.server.business.user.entity.User
import ch.komunumo.server.business.user.entity.UserStatus
import io.jsonwebtoken.Jwts
import mu.KotlinLogging
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.request.header
import org.jetbrains.ktor.util.AttributeKey
import java.security.SecureRandom
import java.time.LocalDateTime

object AuthorizationService {

    val UserAttribute = AttributeKey<User>("user")

    private val codeCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()
    private val minimalCodeLength = 5
    private val maximumCodeLength = 10
    private val thresholdForComplexityIncrease = 20
    private val codeCache: MutableMap<String, OnetimeLoginCode> = mutableMapOf() // TODO clear old entries

    private val signingKey = ConfigurationService.getTokenSigningKey()
    private val logger = KotlinLogging.logger {}

    fun intercept(call: ApplicationCall) {
        val authorization = call.request.header("Authorization")
        if (authorization != null && authorization.toLowerCase().startsWith("bearer")) {
            val token = authorization.split(" ")[1]
            try {
                val claims = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token);
                val email = claims.body["email"] as String?
                if (email != null) {
                    try {
                        val user = UserService.readByEmail(email, UserStatus.ACTIVE)
                        call.attributes.put(UserAttribute, user)
                        logger.info { "User with email '${email}' successfully authorized." }
                    } catch (e: NoSuchElementException) {
                        logger.warn { e.message }
                    }
                }
            } catch (e: Exception) {
                logger.warn(e) { "Can't authorize the user with token '$token'!" }
            }
        }
    }

    fun sendOnetimeLoginCode(email: String) {
        UserService.readByEmail(email, UserStatus.ACTIVE)
        val code = generateCode()
        val validUntil = LocalDateTime.now().plusMinutes(5)
        val onetimeLoginCode = OnetimeLoginCode(email, code, validUntil)
        codeCache.put(email, onetimeLoginCode)
        sendEmail(email, "Komunumo One Time Login Code", "Your Code: ${code}")
        logger.info { "Send onetime login code to user with email '${email}." }
    }

    private fun generateCode(): String {
        val requiredLength = currentlyRequiredChallengeLength()
        val codeBuilder = StringBuilder(requiredLength)
        val random = SecureRandom()
        while (codeBuilder.length < requiredLength) {
            val randomChar = codeCharacters.get(random.nextInt(codeCharacters.size))
            codeBuilder.append(randomChar)
        }
        return codeBuilder.toString()
    }

    private fun currentlyRequiredChallengeLength(): Int {
        val complexityIncrease = (codeCache.size / thresholdForComplexityIncrease)
        val calculatedComplexity = minimalCodeLength + complexityIncrease
        return Math.min(maximumCodeLength, calculatedComplexity)
    }

}

