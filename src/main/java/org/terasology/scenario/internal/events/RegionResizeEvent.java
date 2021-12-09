// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to resize a region entity
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionResizeEvent implements Event {
    private EntityRef entity;
    private BlockRegion region;

    public RegionResizeEvent() {
    }

    public RegionResizeEvent(EntityRef entity, BlockRegionc region) {
        this.entity = entity;
        this.region = new BlockRegion(region);
    }

    public EntityRef getRegionEntity() {
        return entity;
    }

    public BlockRegionc getRegion() {
        return region;
    }
}
