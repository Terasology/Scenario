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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.network.ServerEvent;
import org.terasology.scenario.internal.systems.EntityTreeSystem;
import org.terasology.scenario.internal.ui.LogicTree.LogicTreeValue;

/**
 * Event that is called to move an entity around on the logic screen
 *
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
