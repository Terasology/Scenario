// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Expression component for a Scenario argument entity, Evaluates the name of a player entity
 * <p>
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioExpressionPlayerNameComponent implements Component {
}
