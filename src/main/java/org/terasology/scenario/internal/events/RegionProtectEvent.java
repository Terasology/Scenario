// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called in order to protect or unprotect a region
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionProtectEvent implements Event {
    private EntityRef entity;
    private boolean isProtected;


    public RegionProtectEvent() {
    }

    public RegionProtectEvent(EntityRef entity, boolean isProtected) {
        this.entity = entity;
        this.isProtected = isProtected;
    }

    public EntityRef getRegionEntity() {
        return entity;
    }

    public boolean isProtected() {
        return isProtected;
    }
}
