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

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.modes.loadProcesses.AwaitedLocalCharacterSpawnEvent;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.JomlUtil;
import org.terasology.network.NetworkSystem;
import org.terasology.nui.Color;
import org.terasology.registry.In;
import org.terasology.rendering.logic.FloatingTextComponent;
import org.terasology.rendering.logic.RegionOutlineComponent;
import org.terasology.scenario.components.ScenarioRegionVisibilityComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.RegionAddVisibilityEvent;
import org.terasology.scenario.internal.events.RegionRedrawEvent;
import org.terasology.scenario.internal.events.RegionRemoveVisibilityEvent;
import org.terasology.world.block.BlockRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * System that displays the regions to a client(the 3d box representation and name in the world)
 *
 * Is done on a client to allow for each player to have their own set of displayed regions and not have any effect on other players
 */
@RegisterSystem(RegisterMode.CLIENT)
public class RegionDisplaySystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @In
    private NetworkSystem networkSystem;

    @In
    private LocalPlayer localPlayer;

    private List<EntityRef> regionOutlineAndTextEntities = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(RegionDisplaySystem.class);

    @ReceiveEvent //Check to see if a character has a visibility component, if not then add one, if they do then do cleanup to check for old regions
    public void onComponentActivated(OnActivatedComponent event, EntityRef entity, CharacterComponent component) {
        if (entity.hasComponent(ScenarioRegionVisibilityComponent.class)) { //Character already exists
            ScenarioRegionVisibilityComponent comp = entity.getComponent(ScenarioRegionVisibilityComponent.class);
            List<EntityRef> removalList = new ArrayList<>();
            for (EntityRef e : comp.visibleList) { //Check if any regions were in visible list that don't exist anymore and remove
                if (!e.exists()) {
                    removalList.add(e);
                }
            }
            if (!removalList.isEmpty()) {
                for (EntityRef e : removalList) {
                    comp.visibleList.remove(e);
                }
                entity.saveComponent(comp);
            }
        } else { //Character doesn't have a visibility for regions, so add one
            ScenarioRegionVisibilityComponent newComp = new ScenarioRegionVisibilityComponent();
            entity.addComponent(newComp);
        }
    }


    @ReceiveEvent
    public void onRequestRegionRedrawEvent(RegionRedrawEvent event, EntityRef entity, ScenarioRegionVisibilityComponent component) {
        if (entity.equals(localPlayer.getCharacterEntity())) {
            updateOutlineEntities(component);
        }
    }

    @ReceiveEvent
    public void onAwaitedLocalCharacterSpawnEvent(AwaitedLocalCharacterSpawnEvent event, EntityRef entity, CharacterComponent component) {
        updateOutlineEntities(entity.getComponent(ScenarioRegionVisibilityComponent.class));
    }

    @ReceiveEvent
    public void onChangedVisiblityComponent(OnChangedComponent event, EntityRef entity, ScenarioRegionVisibilityComponent component) {
        if (entity.equals(localPlayer.getCharacterEntity())) { //Only want to watch hub tool visiblity of local player.
            updateOutlineEntities(component);
        }
    }

    @ReceiveEvent
    public void onRegionRemovalVisibilityEvent(RegionRemoveVisibilityEvent event, EntityRef entity, ScenarioRegionVisibilityComponent component) {
        component.visibleList.remove(event.getRemovalEntity());
        entity.saveComponent(component);
    }

    @ReceiveEvent
    public void onRegionAddVisibilityEvent(RegionAddVisibilityEvent event, EntityRef entity, ScenarioRegionVisibilityComponent component) {
        component.visibleList.add(event.getAddEntity());
        entity.saveComponent(component);
    }

    private void updateOutlineEntities(ScenarioRegionVisibilityComponent component) {
        destroyOutlineEntities();
        List<ColoredRegion> regions = getRegionsToDraw(component);

        regionOutlineAndTextEntities.clear();
        for (ColoredRegion r : regions) {
            EntityBuilder entityBuilder = entityManager.newBuilder();
            entityBuilder.setPersistent(false);
            RegionOutlineComponent regionOutlineComponent = new RegionOutlineComponent();
            regionOutlineComponent.corner1 = JomlUtil.from(r.region.getMin(new Vector3i()));
            regionOutlineComponent.corner2 = JomlUtil.from(r.region.getMax(new Vector3i()));
            regionOutlineComponent.color = r.color;
            entityBuilder.addComponent(regionOutlineComponent);
            regionOutlineAndTextEntities.add(entityBuilder.build());

            EntityBuilder entityBuilder2 = entityManager.newBuilder();
            entityBuilder2.setPersistent(false);
            FloatingTextComponent textComponent = new FloatingTextComponent();
            textComponent.scale = 1.0f;
            textComponent.text = r.text;
            textComponent.textColor = r.color;
            LocationComponent loc = new LocationComponent();
            loc.setWorldPosition(r.region.center(new Vector3f()));
            entityBuilder2.addComponent(textComponent);
            entityBuilder2.addComponent(loc);
            regionOutlineAndTextEntities.add(entityBuilder2.build());
        }
    }

    private void destroyOutlineEntities() {
        for(EntityRef e : regionOutlineAndTextEntities) {
            if (e.exists()) {
                e.destroy();
            }
        }
    }

    private class ColoredRegion {
        public BlockRegion region;
        public Color color;
        public String text;
    }

    private List<ColoredRegion> getRegionsToDraw(ScenarioRegionVisibilityComponent component) {
        List<ColoredRegion> returnList = new ArrayList<>();
        for (EntityRef e : component.visibleList) {
            if (e.exists()) {
                ColoredRegion region = new ColoredRegion();
                region.region = e.getComponent(RegionLocationComponent.class).region;
                region.color = e.getComponent(RegionColorComponent.class).color;
                region.text = e.getComponent(RegionNameComponent.class).regionName;
                returnList.add(region);
            }
        }
        return returnList;
    }
}
