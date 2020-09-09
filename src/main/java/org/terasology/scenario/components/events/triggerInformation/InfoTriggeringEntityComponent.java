// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoTriggeringEntityComponent implements Component {
    /**
     * The entity that triggered the event and cause it to occur (Will usually be a player character)
     */
    public EntityRef entity;
}
