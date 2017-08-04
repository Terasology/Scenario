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
package org.terasology.scenario.internal.events;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.scenario.internal.systems.ScenarioRootManagementSystem;
import org.terasology.scenario.internal.systems.ActionEventSystem;

/**
 * An event that is called in order to trigger an action entity to act based on the information entity passed with this event
 * allows for the {@link ScenarioRootManagementSystem} to pass along an information entity that contains information about the triggered event
 *
 * Utilized by {@link ActionEventSystem}
 */
public class EventTriggerEvent implements Event {
    public EntityRef informationEntity;

    public EventTriggerEvent(EntityRef informationEntity) {
        this.informationEntity = informationEntity;
    }
}
