// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to begin a region creation, only sets up a region entity that is in process of being created,
 * does not completely add to the region entity tree yet
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionTreeAddEvent implements Event {
    private EntityRef creatingEntity;

    public RegionTreeAddEvent() {
    }

    public RegionTreeAddEvent(EntityRef creatingEntity) {
        this.creatingEntity = creatingEntity;
    }

    public EntityRef getCreatingEntity() {
        return creatingEntity;
    }
}
