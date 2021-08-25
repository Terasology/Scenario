// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.actions.ScenarioIndicatorActionComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryGiveBlockComponent;
import org.terasology.scenario.components.regions.RegionBeingCreatedComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionContainingEntitiesComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.systems.RegionSystem;
import org.terasology.structureTemplates.components.ProtectedRegionsComponent;

import java.util.ArrayList;
import java.util.List;

/**
 *  Component that indicates an entity is the root scenario entity. Only one entity with this component should exist at a time.
 *  Trigger entities list contain a list of triggers that each have a list of logic entities
 *  Region entities list contains a list of all the region entities
 *
 * Argument entities are detailed in {@link ScenarioArgumentContainerComponent}
 *
 * Typical Scenario logic entities include:
 *   Network Component - This is just the default network component for a terasology entity {@link NetworkComponent}
 *   Indicator Component - This is a component that indicates the general type of the entity(Action/Event/Condition), example is {@link ScenarioIndicatorActionComponent}
 *   {@link ScenarioLogicLabelComponent} - label field includes the text for the dropdown menus
 *   Secondary Component - This is a component that indicates the specific type of the entity, so if it is an action the secondary (indicator) could denote that is is specifically
 *                         a "give block" action, example being {@link ScenarioSecondaryGiveBlockComponent}
 *   *{@link ScenarioLogicTextComponent} - text field is the text that is displayed with arguments included (Detailed in the class)
 *   *{@link ScenarioArgumentContainerComponent} - Only needed if the entity description includes argument parameters
 *
 *   * is not required
 *
 * Region entities include:
 *   Network Component - This is just the default network component for a terasology entity {@link NetworkComponent}
 *   {@link RegionNameComponent} Component - field indicates the name of the region
 *   {@link RegionColorComponent} Component - field indicates the color of the region
 *   {@link RegionContainingEntitiesComponent} Component - field contains a list that is monitored by {@link RegionSystem} of what player entities are within the region
 *   {@link RegionLocationComponent} Component - field is the actual region in the world as a {@link BlockRegion}
 *   * {@link RegionBeingCreatedComponent} Component
 *   * {@link ProtectedRegionsComponent}
 *
 *   * indicates optional (RegionBeingCreated meaning it is currently being created, ProtectedRegion meaning the region
 *     is being protected by the structureTemplates system and will prevent alterations being made to the land within the region
 */
public class ScenarioComponent implements Component<ScenarioComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> triggerEntities  = new ArrayList<>();

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> regionEntities = new ArrayList<>();

    @Override
    public void copyFrom(ScenarioComponent other) {
        this.triggerEntities = Lists.newArrayList(other.triggerEntities);
        this.regionEntities = Lists.newArrayList(other.regionEntities);
    }
}
