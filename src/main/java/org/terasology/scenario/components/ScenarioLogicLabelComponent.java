// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

/**
 * Label component for a Scenario logic entity, includes the text displayed on a dropdown menu for selecting an entity
 * prefab in the logic editor
 * <p>
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class ScenarioLogicLabelComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String name;
}
