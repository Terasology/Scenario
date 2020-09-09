// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.math.geom.Vector3i;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Creation component for a Scenario argument entity, indicates that this region is currently being created
 * <p>
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionBeingCreatedComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Vector3i firstHit;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef creatingEntity;
}
