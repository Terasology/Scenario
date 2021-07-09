// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Expression component for a Scenario argument entity, Evaluates the count of an item in an inventory
 *
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioExpressionBlockCountComponent implements Component<ScenarioExpressionBlockCountComponent> {
}
