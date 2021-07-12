// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information.IndentificationComponents;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Type component for a Scenario argument entity, indicates that the argument is an integer typed value
 *
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioTypeIntegerComponent implements Component<ScenarioTypeIntegerComponent> {
    @Override
    public void copy(ScenarioTypeIntegerComponent other) {

    }
}
