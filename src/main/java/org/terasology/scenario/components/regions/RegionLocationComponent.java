// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.entitySystem.Component;
import org.terasology.network.FieldReplicateType;
import org.terasology.network.Replicate;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.world.block.BlockRegion;

/**
 * Creation component for a Scenario argument entity, contains the actual BlockRegion of the region
 *
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionLocationComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public BlockRegion region;
}
