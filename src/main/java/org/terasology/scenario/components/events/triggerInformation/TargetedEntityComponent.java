// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Component that contains the targeted entity when a logic event is triggered
 */
public class TargetedEntityComponent implements Component {
    /**
     * Entity targeted for a triggered logic event
     */
    public EntityRef entity;
}
