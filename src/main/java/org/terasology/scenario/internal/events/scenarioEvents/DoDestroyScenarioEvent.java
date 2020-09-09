// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.scenarioEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.scenario.components.events.ScenarioSecondaryBlockDestroyComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link
 * ScenarioSecondaryBlockDestroyComponent} to trigger
 */
public class DoDestroyScenarioEvent implements Event {
    private final EntityRef instigator;
    private final EntityRef directCause;
    private final EntityRef destroyed;
    private final Prefab damageType;

    public DoDestroyScenarioEvent(EntityRef instigator, EntityRef directCause, Prefab damageType, EntityRef destroyed) {
        this.instigator = instigator;
        this.directCause = directCause;
        this.damageType = damageType;
        this.destroyed = destroyed;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    public EntityRef getDirectCause() {
        return directCause;
    }

    public Prefab getDamageType() {
        return damageType;
    }

    public EntityRef getDestroyed() {
        return destroyed;
    }
}
