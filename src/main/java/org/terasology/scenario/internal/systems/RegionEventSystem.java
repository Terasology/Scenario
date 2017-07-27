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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.regions.RegionContainingEntitiesComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerEnterRegionEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerLeaveRegionEvent;

import java.util.List;

/**
 * System that will watch regions for all the player entities that enter and leave regions
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionEventSystem extends BaseComponentSystem implements UpdateSubscriberSystem{
    @In
    private EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(RegionEventSystem.class);

    private EntityRef scenario;

    /**
     * Region system's update that monitors the regions and updates them with players entering and leaving regions and triggering
     * events when the respective event occurs to pass to the scenario
     *
     * @param delta The time (in seconds) since the last engine update.
     */
    public void update(float delta) {
        if (scenario == null) {
            if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
                EntityRef tempScen = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
                if (tempScen == null) {
                    return;
                }
                scenario = tempScen;
            }
        }
        else {
            for (EntityRef region : scenario.getComponent(ScenarioComponent.class).regionEntities) {
                RegionLocationComponent locationRegion = region.getComponent(RegionLocationComponent.class);
                RegionContainingEntitiesComponent containing = region.getComponent(RegionContainingEntitiesComponent.class);
                List<EntityRef> tempList = containing.entities;
                for (EntityRef character : entityManager.getEntitiesWith(CharacterComponent.class)) {
                    Vector3f loc = character.getComponent(LocationComponent.class).getWorldPosition();
                    if (locationRegion.region.encompasses((int)loc.x, (int)loc.y, (int)loc.z)) {
                        if (!tempList.contains(character)) { //Just entered region
                            tempList.add(character);
                            PlayerEnterRegionEvent newEvent = new PlayerEnterRegionEvent(character, region);
                            scenario.send(newEvent);
                        }
                    }
                    else {
                        if (tempList.contains(character)) { //Just left region
                            tempList.remove(character);
                            PlayerLeaveRegionEvent newEvent = new PlayerLeaveRegionEvent(character, region);
                            scenario.send(newEvent);
                        }
                    }
                }
                containing.entities = tempList;
                region.saveComponent(containing);
            }
            scenario.saveComponent(scenario.getComponent(ScenarioComponent.class));
        }
    }

}
