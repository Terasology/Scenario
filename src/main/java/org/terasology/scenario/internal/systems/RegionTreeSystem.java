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
package org.terasology.scenario.internal.systems;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.chat.ChatMessageEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.Region3i;
import org.terasology.network.ClientComponent;
import org.terasology.network.ColorComponent;
import org.terasology.registry.In;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.ScenarioHubToolUpdateComponent;
import org.terasology.scenario.components.ScenarioRegionVisibilityComponent;
import org.terasology.scenario.components.regions.RegionBeingCreatedComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.RegionAddVisibilityEvent;
import org.terasology.scenario.internal.events.RegionProtectEvent;
import org.terasology.scenario.internal.events.RegionRecolorEvent;
import org.terasology.scenario.internal.events.RegionRemoveVisibilityEvent;
import org.terasology.scenario.internal.events.RegionRenameEvent;
import org.terasology.scenario.internal.events.RegionResizeEvent;
import org.terasology.scenario.internal.events.RegionTreeAddEvent;
import org.terasology.scenario.internal.events.RegionTreeDeleteEvent;
import org.terasology.scenario.internal.events.RegionTreeFullAddEvent;
import org.terasology.scenario.internal.events.RegionTreeMoveEntityEvent;
import org.terasology.structureTemplates.components.ProtectedRegionsComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * The system that handles all of the events for the entity version of the region tree structure.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionTreeSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @In
    private AssetManager assetManager;

    private EntityRef scenarioEntity;

    private Logger logger = LoggerFactory.getLogger(RegionTreeSystem.class);

    private EntityRef chatMessageEntity;

    @Override
    public void postBegin() {
        Iterable<EntityRef> scenario = entityManager.getEntitiesWith(ScenarioComponent.class); // Checks for existing Scenario

        if (!scenario.iterator().hasNext()) { //No scenario exists yet
            scenarioEntity = entityManager.create(assetManager.getAsset("scenario:scenarioEntity", Prefab.class).get());
        } else {
            scenarioEntity = scenario.iterator().next();
        }

        chatMessageEntity = entityManager.create(assetManager.getAsset("scenario:scenarioChatEntity", Prefab.class).get());
        chatMessageEntity.getComponent(DisplayNameComponent.class).name = "Scenario System";
        chatMessageEntity.saveComponent(chatMessageEntity.getComponent(DisplayNameComponent.class));
        chatMessageEntity.getComponent(ColorComponent.class).color = Color.RED;
        chatMessageEntity.saveComponent(chatMessageEntity.getComponent(ColorComponent.class));
    }

    @ReceiveEvent
    public void onRegionTreeAddEvent(RegionTreeAddEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {

        //Makes sure only one region is being created by a person at a time
        for (EntityRef e : entityManager.getEntitiesWith(RegionBeingCreatedComponent.class)) {
            if (e.getComponent(RegionBeingCreatedComponent.class).creatingEntity.equals(event.getCreatingEntity())) {
                e.destroy();
            }
        }

        event.getCreatingEntity().getOwner().send(new ChatMessageEvent("To begin creation of a region left click a block with a hubtool", chatMessageEntity));

        EntityRef newRegion = entityManager.create(assetManager.getAsset("scenario:scenarioCreationEntity", Prefab.class).get());
        newRegion.getComponent(RegionBeingCreatedComponent.class).creatingEntity = event.getCreatingEntity();
        newRegion.saveComponent(newRegion.getComponent(RegionBeingCreatedComponent.class));

    }

    @ReceiveEvent
    public void onRegionTreeDeleteEvent(RegionTreeDeleteEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        scenarioEntity.getComponent(ScenarioComponent.class).regionEntities.remove(event.getDeleteEntity());
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));


        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.send(new RegionRemoveVisibilityEvent(event.getDeleteEntity()));
            //e.getComponent(ScenarioRegionVisibilityComponent.class).visibleList.remove(event.getDeleteEntity());
            //e.saveComponent(e.getComponent(ScenarioRegionVisibilityComponent.class));
        }

        event.getDeleteEntity().destroy();

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyRegions = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.getComponent(ScenarioRegionVisibilityComponent.class).dirtyRegionsDraw = true;
            e.saveComponent(e.getComponent(ScenarioRegionVisibilityComponent.class));
        }
    }

    @ReceiveEvent
    public void onRegionTreeMoveEntityEvent(RegionTreeMoveEntityEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        List<EntityRef> list = scenarioEntity.getComponent(ScenarioComponent.class).regionEntities;

        int startIndex = list.indexOf(event.getMoveEntity());
        int endIndex = event.getIndex();
        if (startIndex < endIndex) {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex);
        } else {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex + 1);
        }

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyRegions = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    @ReceiveEvent
    public void onRegionTreeFullAddEvent(RegionTreeFullAddEvent event, EntityRef entity, ScenarioComponent component) {
        component.regionEntities.add(event.getAddEntity());
        entity.saveComponent(component);

        entity.saveComponent(component);
        EntityRef addingCharacter = event.getAdder().getOwner().getComponent(ClientComponent.class).character;

        addingCharacter.send(new RegionAddVisibilityEvent(event.getAddEntity()));

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyRegions = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    @ReceiveEvent
    public void onRegionRenameEvent(RegionRenameEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        event.getRegionEntity().getComponent(RegionNameComponent.class).regionName = event.getNewName();
        event.getRegionEntity().saveComponent(event.getRegionEntity().getComponent(RegionNameComponent.class));

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyRegions = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.getComponent(ScenarioRegionVisibilityComponent.class).dirtyRegionsDraw = true;
            e.saveComponent(e.getComponent(ScenarioRegionVisibilityComponent.class));
        }
    }

    @ReceiveEvent
    public void onRegionRecolorEvent(RegionRecolorEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        event.getRegionEntity().getComponent(RegionColorComponent.class).color = event.getNewColor();
        event.getRegionEntity().saveComponent(event.getRegionEntity().getComponent(RegionColorComponent.class));

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyRegions = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.getComponent(ScenarioRegionVisibilityComponent.class).dirtyRegionsDraw = true;
            e.saveComponent(e.getComponent(ScenarioRegionVisibilityComponent.class));
        }
    }

    @ReceiveEvent
    public void onRegionProtectEvent(RegionProtectEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        event.getRegionEntity().removeComponent(ProtectedRegionsComponent.class);
        if (event.isProtected()) {
            ProtectedRegionsComponent protectedRegionsComponent = new ProtectedRegionsComponent();
            List<Region3i> absoluteRegions = Lists.newArrayList();
            absoluteRegions.add(event.getRegionEntity().getComponent(RegionLocationComponent.class).region);
            protectedRegionsComponent.regions = absoluteRegions;
            event.getRegionEntity().addComponent(protectedRegionsComponent);
        }

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    @ReceiveEvent
    public void onRegionResizeEvent(RegionResizeEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        RegionLocationComponent regionLocationComponent = event.getRegionEntity().getComponent(RegionLocationComponent.class);
        regionLocationComponent.region = event.getRegion();
        event.getRegionEntity().saveComponent(regionLocationComponent);

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.getComponent(ScenarioRegionVisibilityComponent.class).dirtyRegionsDraw = true;
            e.saveComponent(e.getComponent(ScenarioRegionVisibilityComponent.class));
        }
    }
}
