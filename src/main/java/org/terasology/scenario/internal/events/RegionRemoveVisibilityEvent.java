// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.components.ScenarioRegionVisibilityComponent;

/**
 * Event that is called in order to remove a region entity to a player character's {@link ScenarioRegionVisibilityComponent}
 */
@OwnerEvent
public class RegionRemoveVisibilityEvent implements Event {
    private EntityRef removalEntity;

    public RegionRemoveVisibilityEvent() {
    }

    public RegionRemoveVisibilityEvent(EntityRef removalEntity) {
        this.removalEntity = removalEntity;
    }

    public EntityRef getRemovalEntity() {
        return removalEntity;
    }
}
