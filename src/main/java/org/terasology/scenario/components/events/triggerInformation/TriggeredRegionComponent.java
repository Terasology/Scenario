// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;
/**
 * Component that contains the triggered region for an event
 */
public class TriggeredRegionComponent implements Component<TriggeredRegionComponent> {
    /**
     * Targeted region for a triggered logic event
     */
    public EntityRef region;
}
