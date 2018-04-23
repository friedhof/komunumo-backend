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

import mu.KotlinLogging
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import java.io.Serializable
import java.nio.file.Paths

object PersistenceManager {

    private val db: DB
    private val logger = KotlinLogging.logger {}

    init {
        val homeDir = System.getProperty("user.home")
        val dbPath = Paths.get(homeDir, ".komunumo", "komunumo.db")
        val dbFile = dbPath.toFile()
        logger.info { "Using persistence store: $dbFile" }
        db = DBMaker.fileDB(dbFile)
                .fileMmapEnableIfSupported()
                .transactionEnable()
                .closeOnJvmShutdown()
                .make()
    }

    @Suppress("UNCHECKED_CAST") // TODO talk to the mapDB developers for a better solution
    fun <T : Serializable> createPersistedMap(name: String): MutableMap<String, T> {
        val hTreeMap = db.hashMap(name)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen() as HTreeMap<String, T>
        return AutoCommitMap(db, hTreeMap)
    }

}

private class AutoCommitMap<K, V>(val db: DB, val map: HTreeMap<K, V>) : MutableMap<K, V> {

    override val size: Int
        get() = map.size

    @Suppress("UNCHECKED_CAST")
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries as MutableSet<MutableMap.MutableEntry<K, V>>

    override val keys: MutableSet<K>
        get() = map.keys

    @Suppress("UNCHECKED_CAST")
    override val values: MutableCollection<V>
        get() = map.values as MutableCollection<V>

    override fun containsKey(key: K): Boolean {
        return map.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return map.containsValue(value)
    }

    override fun get(key: K): V? {
        return map[key]
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun clear() {
        map.clear()
        db.commit()
    }

    override fun put(key: K, value: V): V? {
        val oldValue = map.put(key, value)
        db.commit()
        return oldValue
    }

    override fun putAll(from: Map<out K, V>) {
        map.putAll(from)
        db.commit()
    }

    override fun remove(key: K): V? {
        val oldValue = map.remove(key)
        db.commit()
        return oldValue
    }

}
