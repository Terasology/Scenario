// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.health.DoDestroyEvent;
import org.terasology.engine.logic.players.event.OnPlayerRespawnedEvent;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.internal.events.scenarioEvents.DoDestroyScenarioEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerRespawnScenarioEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerSpawnScenarioEvent;

/**
 * System that listens for normal terasology engine events and converts them into scenario events and sends them to the active scenario in
 * order to for it to invoke any triggers that have a matching Scenario event
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class EventListeningSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(EventListeningSystem.class);

    @ReceiveEvent //Respawn (On death or on rejoin a server)
    public void onPlayerRejoinEvent(OnPlayerRespawnedEvent event, EntityRef entity) {
        if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
            EntityRef scenario = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
            if (scenario == null) {
                return;
            }
            scenario.send(new PlayerRespawnScenarioEvent(entity));
        }
    }

    @ReceiveEvent //Spawn, initial spawn on joining a server
    public void onPlayerSpawnEvent(OnPlayerSpawnedEvent event, EntityRef entity) {
        if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
            EntityRef scenario = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
            if (scenario == null) {
                return;
            }
            scenario.send(new PlayerSpawnScenarioEvent(entity));
        }
    }

    @ReceiveEvent //On block detruction
    public void onDoDestroyEvent(DoDestroyEvent event, EntityRef entity) {
        if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
            EntityRef scenario = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
            if (scenario == null) {
                return;
            }
            scenario.send(new DoDestroyScenarioEvent(event.getInstigator(), event.getDirectCause(), event.getDamageType(), entity));
        }
    }
}
