// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoTargetedEntityComponent implements Component<InfoTargetedEntityComponent> {
    /**
     * entity that was targeted
     */
    public EntityRef entity;

    @Override
    public void copyFrom(InfoTargetedEntityComponent other) {
        this.entity = other.entity;
    }
}
