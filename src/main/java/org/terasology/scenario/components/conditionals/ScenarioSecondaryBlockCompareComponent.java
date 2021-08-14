// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.conditionals;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Secondary Indicator component for a Scenario logic entity, indicates that the entity is a block comparison conditional
 *
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
@Replicate
public class ScenarioSecondaryBlockCompareComponent implements Component<ScenarioSecondaryBlockCompareComponent> {
    @Override
    public void copyFrom(ScenarioSecondaryBlockCompareComponent other) {

    }
}
