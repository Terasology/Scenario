// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.nui.Color;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Creation component for a Scenario argument entity, includes the color of the region for display
 *
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionColorComponent implements Component<RegionColorComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Color color = Color.WHITE;
}
