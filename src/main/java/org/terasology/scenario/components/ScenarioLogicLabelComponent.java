// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Label component for a Scenario logic entity, includes the text displayed on a dropdown menu for selecting an entity prefab in the logic editor
 *
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class ScenarioLogicLabelComponent implements Component<ScenarioLogicLabelComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String name;

    @Override
    public void copy(ScenarioLogicLabelComponent other) {
        this.name = other.name;
    }
}
