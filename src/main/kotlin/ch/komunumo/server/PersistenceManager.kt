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

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import java.io.Serializable
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass

object PersistenceManager {

    private val db: DB

    init {
        db = DBMaker.memoryDB().make()
    }

    @Suppress("UNCHECKED_CAST") // TODO talk to the mapDB developers for a better solution
    fun <T : Serializable> createPersistedMap(name: String, clazz: KClass<out T>): ConcurrentMap<String, T> {
        return db.hashMap(name)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen() as ConcurrentMap<String, T>
    }

}