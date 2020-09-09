// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.internal.utilities.ArgumentParser;

/**
 * Text component that is attached to a Scenario logic entity, the text included is parsed to determine the text that is
 * displayed in the logic editor and what parameters are needed to satisfy the arguments
 * <p>
 * The text should follow a pattern based on the below example: text = "give [player1:Player] [amount:Integer] of
 * [blocktype:Block]" * Parameters follow the pattern of [key:type] where key is the string name of the variable, each
 * key must be unique to the rest of the keys in the entity * the type is the Type of the parameter, this type must be
 * accounted for in {@link ArgumentParser}
 * <p>
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class ScenarioLogicTextComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String text;
}
