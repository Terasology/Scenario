// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Component that contains the entity that triggers an event
 */
public class TriggeringEntityComponent implements Component {
    /**
     * Entity that triggers the event
     */
    public EntityRef entity;
}
