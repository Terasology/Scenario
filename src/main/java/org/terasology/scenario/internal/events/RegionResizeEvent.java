// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.network.ServerEvent;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to resize a region entity
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionResizeEvent implements Event {
    private EntityRef entity;
    private Region3i region;

    public RegionResizeEvent() {
    }

    public RegionResizeEvent(EntityRef entity, Region3i region) {
        this.entity = entity;
        this.region = region;
    }

    public EntityRef getRegionEntity() {
        return entity;
    }

    public Region3i getRegion() {
        return region;
    }
}
