// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, Contains a region entity value
 * <p>
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
public class ScenarioValueRegionComponent implements Component<ScenarioValueRegionComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef regionEntity;

    @Override
    public void copyFrom(ScenarioValueRegionComponent other) {

    }
}
