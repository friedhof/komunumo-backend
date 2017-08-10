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
package ch.komunumo.server.event.control

import ch.komunumo.server.event.entity.Event
import java.util.UUID

object EventService {

    private val events: MutableMap<String, Event> = mutableMapOf()

    fun create(event: Event): String {
        val id = UUID.randomUUID().toString()
        events.put(id, event.copy(id = id))
        return id
    }

    fun readAll(): List<Event> {
        return events.values.toList();
    }

    fun readById(id: String): Event {
        return events.getValue(id)
    }

    fun update(event: Event): Event {
        val id = event.id ?: throw IllegalStateException("The event has no id!")
        if (!events.containsKey(id)) {
            throw NoSuchElementException("There is no event with the id $id!")
        }
        events.put(id, event)
        return event;
    }

}