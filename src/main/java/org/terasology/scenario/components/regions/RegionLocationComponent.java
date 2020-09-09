// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Creation component for a Scenario argument entity, contains the actual Region3i of the region
 * <p>
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionLocationComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Region3i region;
}
