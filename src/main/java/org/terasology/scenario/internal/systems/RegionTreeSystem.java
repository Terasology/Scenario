// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.systems;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.CharacterTeleportEvent;
import org.terasology.engine.logic.chat.ChatMessageEvent;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.network.ColorComponent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.nui.Color;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.ScenarioHubToolUpdateComponent;
import org.terasology.scenario.components.ScenarioRegionVisibilityComponent;
import org.terasology.scenario.components.regions.RegionBeingCreatedComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.HubtoolRewriteRegionEvent;
import org.terasology.scenario.internal.events.RegionAddVisibilityEvent;
import org.terasology.scenario.internal.events.RegionProtectEvent;
import org.terasology.scenario.internal.events.RegionRecolorEvent;
import org.terasology.scenario.internal.events.RegionRedrawEvent;
import org.terasology.scenario.internal.events.RegionRemoveVisibilityEvent;
import org.terasology.scenario.internal.events.RegionRenameEvent;
import org.terasology.scenario.internal.events.RegionResizeEvent;
import org.terasology.scenario.internal.events.RegionTeleportationRequestEvent;
import org.terasology.scenario.internal.events.RegionTreeAddEvent;
import org.terasology.scenario.internal.events.RegionTreeDeleteEvent;
import org.terasology.scenario.internal.events.RegionTreeFullAddEvent;
import org.terasology.scenario.internal.events.RegionTreeMoveEntityEvent;
import org.terasology.structureTemplates.components.ProtectedRegionsComponent;

import java.util.List;

/**
 * The system that handles all of the events for the entity version of the region tree structure.
 * <p>
 * Allows for clients to make request to the entity tree that is contained on the server's side.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionTreeSystem extends BaseComponentSystem {
    private final Logger logger = LoggerFactory.getLogger(RegionTreeSystem.class);
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    private EntityRef scenarioEntity;
    private EntityRef chatMessageEntity;

    @Override
    public void postBegin() {
        Iterable<EntityRef> scenario = entityManager.getEntitiesWith(ScenarioComponent.class); // Checks for existing
        // Scenario

        if (!scenario.iterator().hasNext()) { //No scenario exists yet
            scenarioEntity = entityManager.create(assetManager.getAsset("scenario:scenarioEntity", Prefab.class).get());
        } else {
            scenarioEntity = scenario.iterator().next();
        }

        chatMessageEntity =
                entityManager.create(assetManager.getAsset("scenario:scenarioChatEntity", Prefab.class).get());
        chatMessageEntity.getComponent(DisplayNameComponent.class).name = "Scenario System";
        chatMessageEntity.saveComponent(chatMessageEntity.getComponent(DisplayNameComponent.class));
        chatMessageEntity.getComponent(ColorComponent.class).color = Color.RED;
        chatMessageEntity.saveComponent(chatMessageEntity.getComponent(ColorComponent.class));
    }

    @ReceiveEvent
    public void onRegionTeleportationRequestEvent(RegionTeleportationRequestEvent event, EntityRef entity,
                                                  ScenarioHubToolUpdateComponent component) {
        org.terasology.math.geom.Vector3f location =
                event.getRequestedRegion().getComponent(RegionLocationComponent.class).region.center();
        CharacterTeleportEvent tele = new CharacterTeleportEvent(location);
        event.getTeleportedEntity().send(tele);
    }

    @ReceiveEvent
    public void onRegionTreeAddEvent(RegionTreeAddEvent event, EntityRef entity,
                                     ScenarioHubToolUpdateComponent component) {

        //Makes sure only one region is being created by a person at a time
        for (EntityRef e : entityManager.getEntitiesWith(RegionBeingCreatedComponent.class)) {
            if (e.getComponent(RegionBeingCreatedComponent.class).creatingEntity.equals(event.getCreatingEntity())) {
                e.destroy();
            }
        }

        event.getCreatingEntity().getOwner().send(new ChatMessageEvent("To begin creation of a region left click a " +
                "block with a hubtool", chatMessageEntity));

        EntityRef newRegion = entityManager.create(assetManager.getAsset("scenario:scenarioCreationEntity",
                Prefab.class).get());
        newRegion.getComponent(RegionBeingCreatedComponent.class).creatingEntity = event.getCreatingEntity();
        newRegion.saveComponent(newRegion.getComponent(RegionBeingCreatedComponent.class));

    }

    @ReceiveEvent
    public void onRegionTreeDeleteEvent(RegionTreeDeleteEvent event, EntityRef entity,
                                        ScenarioHubToolUpdateComponent component) {
        scenarioEntity.getComponent(ScenarioComponent.class).regionEntities.remove(event.getDeleteEntity());
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));


        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.send(new RegionRemoveVisibilityEvent(event.getDeleteEntity()));
        }

        event.getDeleteEntity().destroy();

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteRegionEvent());
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.send(new RegionRedrawEvent());
        }
    }

    @ReceiveEvent
    public void onRegionTreeMoveEntityEvent(RegionTreeMoveEntityEvent event, EntityRef entity,
                                            ScenarioHubToolUpdateComponent component) {
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
            e.send(new HubtoolRewriteRegionEvent());
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
            e.send(new HubtoolRewriteRegionEvent());
        }
    }

    @ReceiveEvent
    public void onRegionRenameEvent(RegionRenameEvent event, EntityRef entity,
                                    ScenarioHubToolUpdateComponent component) {
        event.getRegionEntity().getComponent(RegionNameComponent.class).regionName = event.getNewName();
        event.getRegionEntity().saveComponent(event.getRegionEntity().getComponent(RegionNameComponent.class));

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteRegionEvent());
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.send(new RegionRedrawEvent());
        }
    }

    @ReceiveEvent
    public void onRegionRecolorEvent(RegionRecolorEvent event, EntityRef entity,
                                     ScenarioHubToolUpdateComponent component) {
        event.getRegionEntity().getComponent(RegionColorComponent.class).color = event.getNewColor();
        event.getRegionEntity().saveComponent(event.getRegionEntity().getComponent(RegionColorComponent.class));

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteRegionEvent());
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.send(new RegionRedrawEvent());
        }
    }

    @ReceiveEvent
    public void onRegionProtectEvent(RegionProtectEvent event, EntityRef entity,
                                     ScenarioHubToolUpdateComponent component) {
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
    public void onRegionResizeEvent(RegionResizeEvent event, EntityRef entity,
                                    ScenarioHubToolUpdateComponent component) {
        RegionLocationComponent regionLocationComponent =
                event.getRegionEntity().getComponent(RegionLocationComponent.class);
        regionLocationComponent.region = event.getRegion();
        event.getRegionEntity().saveComponent(regionLocationComponent);

        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioRegionVisibilityComponent.class)) {
            e.send(new RegionRedrawEvent());
        }
    }
}
