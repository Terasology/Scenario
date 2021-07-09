// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Creation component for a Scenario argument entity, includes the name of the region
 *
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
@Replicate
public class RegionNameComponent implements Component<RegionNameComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String regionName = "New Region";
}
