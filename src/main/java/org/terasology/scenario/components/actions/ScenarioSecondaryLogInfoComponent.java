// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.actions;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioComponent;

/**
 * Secondary Indicator component for a Scenario logic entity, indicates that the entity is an action that logs to the authoritie's logger
 *
 *
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
@Replicate
public class ScenarioSecondaryLogInfoComponent implements Component<ScenarioSecondaryLogInfoComponent> {
    @Override
    public void copyFrom(ScenarioSecondaryLogInfoComponent other) {

    }
}
