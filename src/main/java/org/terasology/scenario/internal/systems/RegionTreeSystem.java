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

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.chat.ChatMessageEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.network.ClientComponent;
import org.terasology.network.ColorComponent;
import org.terasology.registry.In;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.VisibilityComponent;
import org.terasology.scenario.components.regions.RegionBeingCreatedComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionContainingEntitiesComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.RegionRecolorEvent;
import org.terasology.scenario.internal.events.RegionRenameEvent;
import org.terasology.scenario.internal.events.RegionTreeAddEvent;
import org.terasology.scenario.internal.events.RegionTreeDeleteEvent;
import org.terasology.scenario.internal.events.RegionTreeFullAddEvent;
import org.terasology.scenario.internal.events.RegionTreeMoveEntityEvent;

import java.util.List;

/**
 * The system that handles all of the events for the entity version of the region tree structure.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionTreeSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @ReceiveEvent
    public void onRegionTreeAddEvent(RegionTreeAddEvent event, EntityRef entity, ScenarioComponent component) {
        RegionColorComponent colorComponent = new RegionColorComponent();
        RegionLocationComponent locationComponent = new RegionLocationComponent();
        RegionNameComponent nameComponent = new RegionNameComponent();
        RegionContainingEntitiesComponent contain = new RegionContainingEntitiesComponent();
        RegionBeingCreatedComponent editComponent = new RegionBeingCreatedComponent();

        //Will need to be fixed once creating multiple regions is allowed, should only delete regions
        //currently being created by the person wanting to create a new one
        for (EntityRef e : entityManager.getEntitiesWith(RegionBeingCreatedComponent.class)) {
            e.destroy();
        }


        entityManager.create(colorComponent, locationComponent, nameComponent, contain, editComponent);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getManager().closeScreen(event.getHubScreen());
        }
    }

    @ReceiveEvent
    public void onRegionTreeDeleteEvent(RegionTreeDeleteEvent event, EntityRef entity, ScenarioComponent component) {
        component.regionEntities.remove(event.getDeleteEntity());
        entity.saveComponent(component);


        for (EntityRef e : entityManager.getEntitiesWith(VisibilityComponent.class)) {
            e.getComponent(VisibilityComponent.class).visibleList.remove(event.getDeleteEntity());
            e.saveComponent(e.getComponent(VisibilityComponent.class));
        }

        event.getDeleteEntity().destroy();

        if (event.getHubScreen() != null) {
            event.getHubScreen().updateRegionTree(entity);
        }


    }

    @ReceiveEvent
    public void onRegionTreeMoveEntityEvent(RegionTreeMoveEntityEvent event, EntityRef entity, ScenarioComponent component) {
        List<EntityRef> list = component.regionEntities;

        int startIndex = list.indexOf(event.getMoveEntity());
        int endIndex = event.getIndex();
        if (startIndex < endIndex) {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex);
        }
        else {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex + 1);
        }

        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().updateRegionTree(entity);
        }
    }

    @ReceiveEvent
    public void onRegionTreeFullAddEvent(RegionTreeFullAddEvent event, EntityRef entity, ScenarioComponent component) {
        component.regionEntities.add(event.getAddEntity());
        entity.saveComponent(component);

        DisplayNameComponent name = new DisplayNameComponent();
        name.name = "Scenario System";
        ColorComponent color = new ColorComponent();
        color.color = Color.RED;
        EntityRef ent = entityManager.create(name, color);

        EntityRef clientInfo = event.getAdder().getOwner().getComponent(ClientComponent.class).clientInfo;
        String displayName = FontColor.getColored(clientInfo.getComponent(DisplayNameComponent.class).name, clientInfo.getComponent(ColorComponent.class).color);

        for (EntityRef client : entityManager.getEntitiesWith(ClientComponent.class)) {
            client.send(new ChatMessageEvent("Region created by " + displayName, ent));
        }

        entity.saveComponent(component);
        for (EntityRef e : entityManager.getEntitiesWith(VisibilityComponent.class)) {
            e.getComponent(VisibilityComponent.class).visibleList.add(event.getAddEntity());
            e.saveComponent(e.getComponent(VisibilityComponent.class));
        }

        ent.destroy();
    }

    @ReceiveEvent
    public void onRegionRenameEvent(RegionRenameEvent event, EntityRef entity, ScenarioComponent component) {
        event.getRegionEntity().getComponent(RegionNameComponent.class).regionName = event.getNewName();
        event.getRegionEntity().saveComponent(event.getRegionEntity().getComponent(RegionNameComponent.class));

        entity.saveComponent(component);
        if (event.getHubScreen() != null) {
            event.getHubScreen().updateRegionTree(entity);
        }
    }

    @ReceiveEvent
    public void onRegionRecolorEvent(RegionRecolorEvent event, EntityRef entity, ScenarioComponent component) {
        event.getRegionEntity().getComponent(RegionColorComponent.class).color = event.getNewColor();
        event.getRegionEntity().saveComponent(event.getRegionEntity().getComponent(RegionColorComponent.class));

        entity.saveComponent(component);
        if (event.getHubScreen() != null) {
            event.getHubScreen().updateRegionTree(entity);
        }
    }
}
