// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.EntityTreeSystem;

/**
 * Event for deleting an entity from the logic tree. Sent to the root entity. deleteEntity: The entity that is requested to be deleted.
 * deleteFromEntity: The entity that the deleteEntity is attached to(trigger if entity is a event/condition/action, scenario if it is a
 * trigger) hubScreen: The hub tool's screen, if this is passed then after adding the event it will update the tree on this hubScreen, if
 * not passed(null) then it will not update immediately.
 * <p>
 * Utilized with {@link EntityTreeSystem}
 */
@ServerEvent
public class LogicTreeDeleteEvent implements Event {
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
