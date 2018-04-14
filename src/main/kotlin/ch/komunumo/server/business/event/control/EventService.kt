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
package ch.komunumo.server.business.event.control

import ch.komunumo.server.PersistenceManager
import ch.komunumo.server.business.event.entity.Event
import ch.komunumo.server.business.generateNewUniqueId
import java.util.ConcurrentModificationException


object EventService {

    private val events: MutableMap<String, Event> = PersistenceManager.createPersistedMap("events")

    fun create(event: Event): String {
        val id = generateNewUniqueId(events.keys)
        val version = event.hashCode()
        events[id] = event.copy(id = id, version = version)
        return id
    }

    fun readAll(): List<Event> {
        return events.values.toList()
    }

    fun readById(id: String): Event {
        return events.getValue(id)
    }

    fun update(event: Event): Event {
        val id = event.id ?: throw IllegalStateException("The event has no id!")
        val oldEvent = readById(id)
        if (oldEvent.version != event.version) {
            throw ConcurrentModificationException("The event with id '$id' was modified concurrently!")
        }
        val version = event.hashCode()
        val newEvent = event.copy(id = id, version = version)
        events[id] = newEvent
        return newEvent
    }

}
