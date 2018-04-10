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
package ch.komunumo.server.business.user.control

import ch.komunumo.server.PersistenceManager
import ch.komunumo.server.business.configuration.control.ConfigurationService
import ch.komunumo.server.business.generateNewUniqueId
import ch.komunumo.server.business.user.entity.User
import ch.komunumo.server.business.user.entity.UserRole
import ch.komunumo.server.business.user.entity.UserStatus
import mu.KotlinLogging
import java.util.ConcurrentModificationException

object UserService {

    private val users: MutableMap<String, User>
    private val logger = KotlinLogging.logger {}

    init {
        users = PersistenceManager.createPersistedMap("users", User::class)
        createAdminUserFromConfiguration()
    }

    private fun createAdminUserFromConfiguration() {
        if (ConfigurationService.getAdminEmail().isNotBlank()) {
            try {
                val adminUser = User(
                        firstName = ConfigurationService.getAdminFirstname(),
                        lastName = ConfigurationService.getAdminLastname(),
                        email = ConfigurationService.getAdminEmail(),
                        role = UserRole.ADMIN,
                        status = UserStatus.ACTIVE)
                create(adminUser)
                logger.info { "Admin user successfully created." }
            } catch (e: IllegalArgumentException) {
                logger.info { "Admin user already exists." }
            }
        }
    }

    fun create(user: User): String {
        try {
            val email = readByEmail(user.email).email
            throw IllegalArgumentException("There is already an user with email '${email}'!")
        } catch (e: NoSuchElementException) {
            val id = generateNewUniqueId(users.keys)
            val version = user.hashCode()
            users.put(id, user.copy(id = id, version = version))
            return id
        }
    }

    fun readAll(): List<User> {
        return users.values.toList();
    }

    fun readById(id: String): User {
        return users.getValue(id)
    }

    fun readByEmail(email: String) : User {
        return users.values.stream()
                .filter { user -> user.email == email }
                .findFirst()
                .orElseThrow { NoSuchElementException("No user with email '$email' found!") }
    }

    fun readByEmail(email: String, status: UserStatus) : User {
        val user = readByEmail(email);
        if (user.status != status) {
            throw NoSuchElementException("The user with email '$email' is not '$status'!")
        }
        return user
    }

    fun update(user: User): User {
        val id = user.id ?: throw IllegalStateException("The user has no id!")
        val oldUser = readById(id)
        if (oldUser.version != user.version) {
            throw ConcurrentModificationException("The user with id '$id' was modified concurrently!")
        }
        val version = user.hashCode()
        val newUser = user.copy(id = id, version = version)
        users.put(id, newUser)
        return newUser;
    }

}
