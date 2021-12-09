// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to delete a region from the region tree
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionTreeDeleteEvent implements Event {
    private EntityRef deleteEntity;

    public RegionTreeDeleteEvent() {
    }

    public RegionTreeDeleteEvent(EntityRef deleteEntity) {
        this.deleteEntity = deleteEntity;
    }

    public EntityRef getDeleteEntity() {
        return deleteEntity;
    }
}
