// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.scenarioEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.scenario.components.events.ScenarioSecondarySpawnComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link
 * ScenarioSecondarySpawnComponent} to trigger
 */
public class PlayerSpawnScenarioEvent implements Event {
    private final EntityRef spawningEntity;

    public PlayerSpawnScenarioEvent(EntityRef spawningEntity) {
        this.spawningEntity = spawningEntity;
    }

    public EntityRef getSpawningEntity() {
        return spawningEntity;
    }
}
