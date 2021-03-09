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
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeIntegerComponent;
import org.terasology.scenario.components.information.ScenarioValueStringComponent;

import java.util.Map;

/**
 * Argument Container component for a Scenario logic entity, NEEDS to be includes if the description includes argument parameters
 *
 * Argument entities include:
 *   Network Component - This is just the default network component for a terasology entity {@link NetworkComponent}
 *   Type Component - This is a component used to denote the type of variable that the entity is representing, example is {@link ScenarioTypeIntegerComponent}
 *   Value or Expression Component (Values are constant values, expressions are evaluated to obtain the value)
 *        - This is the component that holds the actual value or expression representation for the entity, example is {@link ScenarioValueStringComponent}
 *
 * Scenario logic entities are detailed in {@link ScenarioComponent}
 */
@Replicate
public class ScenarioArgumentContainerComponent implements Component {
    @Replicate
    public Map<String, EntityRef> arguments;
}
