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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;

import java.util.Map;

/**
 * Argument Container component for a Scenario logic entity, NEEDS to be includes if the description includes argument parameters
 *
 * Argument entities include:
 *   Network Component
 *   Type Component
 *   Value or Expression Component (Values are constant values, expressions are evaluated to obtain the value)
 *
 * Typical Scenario logic entities include:
 *   Network Component
 *   Indicator Component
 *   {@link ScenarioLogicLabelComponent} - label field includes the text for the dropdown menus
 *   Secondary Component
 *   *{@link ScenarioLogicTextComponent} - text field is the text that is displayed with arguments included (Detailed in the class)
 *   *{@link ScenarioArgumentContainerComponent} - Only needed if the entity description includes argument parameters
 *
 *   * is not required
 */
@Replicate
public class ScenarioArgumentContainerComponent implements Component {
    @Replicate
    public Map<String, EntityRef> arguments;
}
