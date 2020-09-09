// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.scenario.internal.systems.ActionEventSystem;
import org.terasology.scenario.internal.systems.ScenarioRootManagementSystem;

/**
 * An event that is called in order to trigger an action entity to act based on the information entity passed with this
 * event allows for the {@link ScenarioRootManagementSystem} to pass along an information entity that contains
 * information about the triggered event
 * <p>
 * Utilized by {@link ActionEventSystem}
 */
public class EventTriggerEvent implements Event {
    public EntityRef informationEntity;

    public EventTriggerEvent(EntityRef informationEntity) {
        this.informationEntity = informationEntity;
    }
}
