/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.EntityTreeSystem;

/**
 * Event for deleting an entity from the logic tree. Sent to the root entity.
 * deleteEntity: The entity that is requested to be deleted.
 * deleteFromEntity: The entity that the deleteEntity is attached to(trigger if entity is a event/condition/action, scenario if it is a trigger)
 * hubScreen: The hub tool's screen, if this is passed then after adding the event it will update the tree on this hubScreen,
 *            if not passed(null) then it will not update immediately.
 *
 * Utilized with {@link EntityTreeSystem}
 */
@ServerEvent
public class LogicTreeDeleteEvent implements Event{
    private EntityRef deleteEntity;
    private EntityRef deleteFromEntity;

    public LogicTreeDeleteEvent() {
    }

    public LogicTreeDeleteEvent(EntityRef deleteEntity, EntityRef deleteFromEntity) {
        this.deleteEntity = deleteEntity;
        this.deleteFromEntity = deleteFromEntity;
    }

    public EntityRef getDeleteEntity() {
        return deleteEntity;
    }

    public EntityRef getDeleteFromEntity() {
        return deleteFromEntity;
    }
}
