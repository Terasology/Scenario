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
package org.terasology.scenario.components.events;

import org.terasology.entitySystem.Component;
import org.terasology.network.Replicate;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.ScenarioLogicLabelComponent;
import org.terasology.scenario.components.ScenarioLogicTextComponent;

/**
 * Secondary component for a Scenario logic entity, indicates that the entity is an event based on the player first spawning
 *
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
public class ScenarioSecondarySpawnComponent implements Component {
}
