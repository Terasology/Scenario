// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Creation component for a Scenario argument entity, indicates that this region is currently being created
 *
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionBeingCreatedComponent implements Component<RegionBeingCreatedComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Vector3i firstHit;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef creatingEntity;

    @Override
    public void copyFrom(RegionBeingCreatedComponent other) {
        this.firstHit = other.firstHit;
        this.creatingEntity = other.creatingEntity;
    }
}
