// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Component that contains the triggered region for an event
 */
public class TriggeredRegionComponent implements Component {
    /**
     * Targeted region for a triggered logic event
     */
    public EntityRef region;
}
