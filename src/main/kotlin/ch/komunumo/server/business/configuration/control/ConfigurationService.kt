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

import java.io.FileInputStream
import java.nio.file.Paths
import java.util.Properties



object ConfigurationService {

    private val configurationFilename = "komunumo.cfg"

    private val properties: Properties

    init {
        properties = loadConfiguration()
    }

    private fun loadConfiguration(): Properties {
        val properties = Properties()
        val file = Paths.get(System.getProperty("user.home"), ".komunumo", configurationFilename).toFile()
        if (file.exists()) {
            FileInputStream(file).use { stream -> properties.load(stream) }
        }
        return properties
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

}