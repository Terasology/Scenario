// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component that contains the entity that triggers an event
 */
public class TriggeringEntityComponent implements Component<TriggeringEntityComponent> {
    /**
     * Entity that triggers the event
     */
    public EntityRef entity;
}
