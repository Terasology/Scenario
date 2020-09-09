// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to rename a region entity
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionRenameEvent implements Event {
    private EntityRef entity;
    private String newName;

    public RegionRenameEvent() {
    }

    public RegionRenameEvent(EntityRef entity, String newName) {
        this.entity = entity;
        this.newName = newName;
    }

    public EntityRef getRegionEntity() {
        return entity;
    }

    public String getNewName() {
        return newName;
    }
}
