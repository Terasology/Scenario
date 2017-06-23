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
package org.terasology.scenario.components.actions;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.scenario.components.information.InformationEnums;
import org.terasology.world.block.BlockManager;

import java.util.List;
import java.util.Map;

public interface ActionComponent extends Component {
    /**
     * Text for the display based on a regular expression being used to read through it. Parameters are detailed
     * with [parameterVariable]. This variable must match the variable name used in the map for value type.
     *
     * For example:
     * "Give [player] [amount] of [item]"
     */
    String getParsableText();

    /**
     * Text for the display on the node. Relies on the component to convert it's inner variables into strings
     */
    String getDisplayText();

    /**
     * Returns the mapping for this component based on the above
     */
    Map<String, EntityRef> getMapping(EntityManager entityManager);

    /**
     * Returns the types for this component's variables based on the above
     */
    Map<String, InformationEnums.DataTypes> getTypes();

    /**
     * Returns the variables and their types needed to be filled to trigger the action
     */
    Map<String, InformationEnums.DataTypes> neededTypes();

    /**
     * Triggers this action based on the required values indicated in the mapping from neededTypes().
     * Component will delete the entityRefs after use
     */
    void triggerAction(Map<String, EntityRef> variables, EntityManager entityManager);

    /**
     * Allows for setting a variable in the component,
     * Component will delete the old entityRef that is being replaced
     */
    void setVariable(ActionVariable variable);

    /**
     * Access to a single variable
     */
    EntityRef getVariable(String variableName);


    class ActionVariable{
        public String variable;
        public EntityRef value;

        ActionVariable(String variable, EntityRef value){
            this.variable = variable;
            this.value = value;
        }
    }


}
