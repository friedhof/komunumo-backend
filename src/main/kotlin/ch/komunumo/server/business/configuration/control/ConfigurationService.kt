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
package ch.komunumo.server.business.configuration.control

import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Paths
import java.util.Properties

object ConfigurationService {

    private const val defaultConfigurationFilename = "komunumo.cfg"
    private val logger = KotlinLogging.logger {}
    private const val environmentConfigurationFilename = "KOMUNUMOCONFIG"
    private val properties: Properties

    init {
        properties = loadConfiguration()
    }

    private fun loadConfiguration(): Properties {
        val properties = Properties()
        val file: File = configFile()


        if (file.exists()) {
            logger.info { "Using configuration: " + file.toString() }
            println("Using configuration: " + file.toString())

            FileInputStream(file).use { stream -> properties.load(stream) }
        } else {
//            logger.info { "No configuration file found under " + file.toString() }
//            println("No configuration file found under " + file.toString())

            throw FileNotFoundException("Configuration file not found at $file")
        }

        return properties
    }

    private fun configFile(): File {
        val filePath = System.getenv(environmentConfigurationFilename) ?: ""

        var filePathLocal = filePath
        var file: File
        if (!filePathLocal.contentEquals("")) {
            file = Paths.get(filePathLocal).toFile()
            if (!file.exists()) {
                val workingDir = System.getProperty("user.dir")
                filePathLocal = Paths.get(workingDir, filePathLocal).toString()
            }

        } else {
            filePathLocal = Paths.get(System.getProperty("user.home"), ".komunumo", defaultConfigurationFilename).toString()

        }

        file = Paths.get(filePathLocal).toFile()
        return file
    }

    private fun getString(key: String, default: String): String {
        return properties.getProperty(key, default)
    }

    private fun getInt(key: String, default: Int): Int {
        return Integer.valueOf(getString(key, default.toString()))
    }

    private fun getBoolean(key: String, default: Boolean): Boolean {
        return java.lang.Boolean.valueOf(getString(key, default.toString()))
    }

    fun getTokenSigningKey(): String {
        return getString("token.signing.key", "")
    }

    fun getDBFilePath(): String {
        return getString("database.path", "")
    }

    fun getTokenExpirationTime(): Int {
        return getInt("token.expiration.time", 60 * 24) // in minutes, default = 24h
    }

    fun getAdminFirstname(): String {
        return getString("admin.firstName", "")
    }

    fun getAdminLastname(): String {
        return getString("admin.lastName", "Admin")
    }

    fun getAdminEmail(): String {
        return getString("admin.email", "root@localhost")
    }

    fun getSMTPServer(): String {
        return getString("smtp.server", "localhost")
    }

    fun getSMTPPort(): Int {
        return getInt("smtp.port", 25)
    }

    fun getSMTPUser(): String {
        return getString("smtp.user", System.getProperty("user.name"))
    }

    fun getSMTPPassword(): String {
        return getString("smtp.password", "")
    }

    fun getSMTPSSL(): Boolean {
        return getBoolean("smtp.useSSL", false)
    }

    fun getSMTPFrom(): String {
        return getString("smtp.from", System.getProperty("user.name") + "@localhost")
    }

    fun getServerBaseURL(): String {
        return getString("server.baseURL", "http://localhost:8080")
    }

}
