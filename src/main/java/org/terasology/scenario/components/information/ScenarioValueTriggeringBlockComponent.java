// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, indicates that this argument should use the triggering block entity
 * <p>
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioValueTriggeringBlockComponent implements Component {
}
