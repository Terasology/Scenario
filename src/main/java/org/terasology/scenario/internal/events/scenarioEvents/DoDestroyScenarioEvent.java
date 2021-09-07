/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.scenario.internal.events.scenarioEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.components.events.ScenarioSecondaryBlockDestroyComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link ScenarioSecondaryBlockDestroyComponent}
 * to trigger
 */
public class DoDestroyScenarioEvent implements Event {
    private EntityRef instigator;
    private EntityRef directCause;
    private EntityRef destroyed;
    private Prefab damageType;

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
