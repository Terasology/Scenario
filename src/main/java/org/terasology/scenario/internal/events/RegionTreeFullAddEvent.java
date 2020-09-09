// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called when a region is fully created and needs to be added to the region tree
 * <p>
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionTreeFullAddEvent implements Event {
    private EntityRef addEntity;
    private EntityRef adder;

    public RegionTreeFullAddEvent() {
    }

    public RegionTreeFullAddEvent(EntityRef addEntity, EntityRef adder) {
        this.addEntity = addEntity;
        this.adder = adder;
    }

    public EntityRef getAddEntity() {
        return addEntity;
    }

    public EntityRef getAdder() {
        return adder;
    }
}
