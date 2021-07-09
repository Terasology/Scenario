// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component that contains the targeted entity when a logic event is triggered
 */
public class TargetedEntityComponent implements Component<TargetedEntityComponent> {
    /**
     * Entity targeted for a triggered logic event
     */
    public EntityRef entity;
}
