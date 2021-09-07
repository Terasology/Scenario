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
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.components.events.ScenarioSecondaryLeaveRegionComponent;

/**
 * Event that is a part of the scenario module that will invoke any logic entities with {@link ScenarioSecondaryLeaveRegionComponent}
 * to trigger
 */
public class PlayerLeaveRegionEvent implements Event {
    private EntityRef entity;
    private EntityRef region;

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
