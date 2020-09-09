// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoTargetedEntityComponent implements Component {
    /**
     * entity that was targeted
     */
    public EntityRef entity;
}
