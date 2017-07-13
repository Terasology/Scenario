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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.rendering.logic.FloatingTextComponent;
import org.terasology.rendering.logic.RegionOutlineComponent;
import org.terasology.rendering.nui.Color;
import org.terasology.scenario.components.VisibilityComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.CLIENT)
public class RegionDisplaySystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @In
    private LocalPlayer localPlayer;

    private List<EntityRef> regionOutlineAndTextEntities = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(RegionDisplaySystem.class);
    
    @ReceiveEvent
    public void onChangedVisiblityComponent(OnChangedComponent event, EntityRef entity, VisibilityComponent component) {
        if (entity.getOwner().equals(localPlayer.getCharacterEntity())) { //Only want to watch hub tool visiblity of local player
            updateOutlineEntities(component);
        }
    }



    private void updateOutlineEntities(VisibilityComponent component) {
        destroyOutlineEntities();
        List<ColoredRegion> regions = getRegionsToDraw(component);

        regionOutlineAndTextEntities.clear();
        for (ColoredRegion r : regions) {
            EntityBuilder entityBuilder = entityManager.newBuilder();
            entityBuilder.setPersistent(false);
            RegionOutlineComponent regionOutlineComponent = new RegionOutlineComponent();
            regionOutlineComponent.corner1 = new Vector3i(r.region.min());
            regionOutlineComponent.corner2 = new Vector3i(r.region.max());
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
            loc.setWorldPosition(r.region.center());
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
        public Region3i region;
        public Color color;
        public String text;
    }

    private List<ColoredRegion> getRegionsToDraw(VisibilityComponent component) {
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
