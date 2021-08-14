// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoTriggeringEntityComponent implements Component<InfoTriggeringEntityComponent> {
    /**
     * The entity that triggered the event and cause it to occur (Will usually be a player character)
     */
    public EntityRef entity;

    @Override
    public void copyFrom(InfoTriggeringEntityComponent other) {
        this.entity = other.entity;
    }
}
