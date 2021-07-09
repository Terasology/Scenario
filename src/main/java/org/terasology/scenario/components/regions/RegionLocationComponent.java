// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Creation component for a Scenario argument entity, contains the actual BlockRegion of the region
 *
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionLocationComponent implements Component<RegionLocationComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public BlockRegion region;
}
