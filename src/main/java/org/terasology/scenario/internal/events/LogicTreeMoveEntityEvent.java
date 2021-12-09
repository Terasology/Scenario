// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.EntityTreeSystem;
import org.terasology.scenario.internal.ui.LogicTree.LogicTreeValue;

/**
 * Event that is called to move an entity around on the logic screen
 * <p>
 * Utilized with {@link EntityTreeSystem}
 */
@ServerEvent
public class LogicTreeMoveEntityEvent implements Event {
    private EntityRef triggerEntity;
    private EntityRef moveEntity;
    private LogicTreeValue.Type elementType;
    private int index;

    public LogicTreeMoveEntityEvent() {
    }

    public LogicTreeMoveEntityEvent(EntityRef triggerEntity, EntityRef moveEntity, LogicTreeValue.Type elementType, int index) {
        this.triggerEntity = triggerEntity;
        this.moveEntity = moveEntity;
        this.elementType = elementType;
        this.index = index;
    }

    public EntityRef getTriggerEntity() {
        return triggerEntity;
    }

    public EntityRef getMoveEntity() {
        return moveEntity;
    }

    public LogicTreeValue.Type getElementType() {
        return elementType;
    }

    public int getIndex() {
        return index;
    }
}
