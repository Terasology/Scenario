// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;
/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoTriggerRegionComponent implements Component<InfoTriggerRegionComponent> {
    /**
     * The entity of the region that was triggered (example: the joined region or the region that was just exited)
     */
    public EntityRef region;

    @Override
    public void copy(InfoTriggerRegionComponent other) {
        this.region = other.region;
    }
}
