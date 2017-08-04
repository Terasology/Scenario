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
import org.terasology.math.Region3i;
import org.terasology.network.FieldReplicateType;
import org.terasology.network.Replicate;
import org.terasology.scenario.internal.systems.RegionSystem;
import org.terasology.structureTemplates.components.ProtectedRegionsComponent;

import java.util.ArrayList;
import java.util.List;

/**
 *  Component that indicates an entity is the root scenario entity. Only one entity with this component should exist at a time.
 *  Trigger entities list contain a list of triggers that each have a list of logic entities
 *  Region entities list contains a list of all the region entities
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
 *
 * Region entities include:
 *   Network Component
 *   RegionName Component - field indicates the name of the region
 *   RegionColor Component - field indicates the color of the region
 *   RegionContainingEntities Component - field contains a list that is monitored by {@link RegionSystem} of what player entities are within the region
 *   RegionLocation Component - field is the actual region in the world as a {@link Region3i}
 *   * RegionBeingCreated Component
 *   * {@link ProtectedRegionsComponent}
 *
 *   * indicates optional (RegionBeingCreated meaning it is currently being created, ProtectedRegion meaning the region
 *     is being protected by the structureTemplates system and will prevent alterations being made to the land within the region
 */
public class ScenarioComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> triggerEntities  = new ArrayList<>();

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> regionEntities = new ArrayList<>();
}
