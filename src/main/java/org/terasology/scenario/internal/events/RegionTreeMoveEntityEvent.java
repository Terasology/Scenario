// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to move a region in the region tree
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionTreeMoveEntityEvent implements Event {
    private EntityRef moveEntity;
    private int index;

    public RegionTreeMoveEntityEvent() {
    }

    public RegionTreeMoveEntityEvent(EntityRef moveEntity, int index) {
        this.moveEntity = moveEntity;
        this.index = index;
    }

    public EntityRef getMoveEntity() {
        return moveEntity;
    }

    public int getIndex() {
        return index;
    }
}
