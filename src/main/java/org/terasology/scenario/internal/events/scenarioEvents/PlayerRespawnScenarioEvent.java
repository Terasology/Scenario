// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.scenarioEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.components.events.ScenarioSecondaryRespawnComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link ScenarioSecondaryRespawnComponent} to
 * trigger
 */
public class PlayerRespawnScenarioEvent implements Event {
    private final EntityRef spawningEntity;

    public PlayerRespawnScenarioEvent(EntityRef spawningEntity) {
        this.spawningEntity = spawningEntity;
    }

    public EntityRef getSpawningEntity() {
        return spawningEntity;
    }
}
