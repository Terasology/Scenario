// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.scenarioEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.components.events.ScenarioSecondaryEnterRegionComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link ScenarioSecondaryEnterRegionComponent} to
 * trigger
 */
public class PlayerEnterRegionEvent implements Event {
    private final EntityRef entity;
    private final EntityRef region;

    public PlayerEnterRegionEvent(EntityRef entity, EntityRef region) {
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
