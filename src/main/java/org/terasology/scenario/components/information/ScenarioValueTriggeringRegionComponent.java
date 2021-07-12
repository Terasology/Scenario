// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, indicates that this argument should use the triggering region entity
 *
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
public class ScenarioValueTriggeringRegionComponent implements Component<ScenarioValueTriggeringRegionComponent> {
    @Override
    public void copy(ScenarioValueTriggeringRegionComponent other) {

    }
}
