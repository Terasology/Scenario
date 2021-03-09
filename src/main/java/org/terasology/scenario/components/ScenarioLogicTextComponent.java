/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.internal.utilities.ArgumentParser;

/**
 * Text component that is attached to a Scenario logic entity, the text included is parsed to determine the text that is
 * displayed in the logic editor and what parameters are needed to satisfy the arguments
 *
 * The text should follow a pattern based on the below example:
 *      text = "give [player1:Player] [amount:Integer] of [blocktype:Block]"
 *      * Parameters follow the pattern of [key:type] where key is the string name of the variable, each key must be unique to the rest of the keys in the entity
 *      * the type is the Type of the parameter, this type must be accounted for in {@link ArgumentParser}
 *
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class ScenarioLogicTextComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String text;
}
