// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.math.geom.Vector3f;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.regions.RegionContainingEntitiesComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerEnterRegionEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerLeaveRegionEvent;

import java.util.List;

/**
 * System that will watch regions for all the player character entities that enter and leave regions
 * <p>
 * Will update the {@link RegionContainingEntitiesComponent} of all the scenario region entities that are being
 * monitored
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionEventSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private final Logger logger = LoggerFactory.getLogger(RegionEventSystem.class);
    @In
    private EntityManager entityManager;
    private EntityRef scenario;

    /**
     * Region system's update that monitors the regions and updates them with players entering and leaving regions and
     * triggering events when the respective event occurs to pass to the scenario
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
        } else {
            for (EntityRef region : scenario.getComponent(ScenarioComponent.class).regionEntities) {
                RegionLocationComponent locationRegion = region.getComponent(RegionLocationComponent.class);
                RegionContainingEntitiesComponent containing =
                        region.getComponent(RegionContainingEntitiesComponent.class);
                List<EntityRef> tempList = containing.entities;
                for (EntityRef character : entityManager.getEntitiesWith(CharacterComponent.class)) {
                    Vector3f loc = character.getComponent(LocationComponent.class).getWorldPosition();
                    if (locationRegion.region.encompasses((int) loc.x, (int) loc.y, (int) loc.z)) {
                        if (!tempList.contains(character)) { //Just entered region
                            tempList.add(character);
                            PlayerEnterRegionEvent newEvent = new PlayerEnterRegionEvent(character, region);
                            scenario.send(newEvent);
                        }
                    } else {
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
