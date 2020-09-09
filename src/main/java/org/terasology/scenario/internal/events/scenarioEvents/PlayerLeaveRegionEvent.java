// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.scenarioEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.scenario.components.events.ScenarioSecondaryLeaveRegionComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link
 * ScenarioSecondaryLeaveRegionComponent} to trigger
 */
public class PlayerLeaveRegionEvent implements Event {
    private final EntityRef entity;
    private final EntityRef region;

    public PlayerLeaveRegionEvent(EntityRef entity, EntityRef region) {
        this.entity = entity;
        this.region = region;
    }

    public EntityRef getTriggerEntity() {
        return entity;
    }

    public EntityRef getRegion() {
        return region;
    }
}
