// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.EmptyComponent;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, Contains a player type(Targeted or Triggering) value
 * <p>
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
public class ScenarioValuePlayerComponent extends EmptyComponent<ScenarioValuePlayerComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public PlayerType type = PlayerType.TRIGGERING_PLAYER;

    public enum PlayerType {
        TRIGGERING_PLAYER,
        TARGETED_PLAYER,
    }
}
