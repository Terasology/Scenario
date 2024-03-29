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
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.TriggerActionListComponent;
import org.terasology.scenario.components.TriggerConditionListComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryBlockDestroyComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryEnterRegionComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryLeaveRegionComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryRespawnComponent;
import org.terasology.scenario.components.events.ScenarioSecondarySpawnComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoDestroyedBlockComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoTriggerRegionComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoTriggeringEntityComponent;
import org.terasology.scenario.internal.events.EventTriggerEvent;
import org.terasology.scenario.internal.events.evaluationEvents.ConditionalCheckEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateRegionEvent;
import org.terasology.scenario.internal.events.scenarioEvents.DoDestroyScenarioEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerEnterRegionEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerLeaveRegionEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerRespawnScenarioEvent;
import org.terasology.scenario.internal.events.scenarioEvents.PlayerSpawnScenarioEvent;

/**
 * System that relays game events into scenario events and sends them using a filled up information entity that contains information of the
 * trigger which could include who the triggering entity or region is, or block details for breaking the block, etc
 * <p>
 * First checks any conditionals with a {@link ConditionalCheckEvent} and if the conditional is satisfies it Will send {@link
 * EventTriggerEvent} to the attached list of actions, technically in the order of the actions on the hubtool
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ScenarioRootManagementSystem extends BaseComponentSystem {
    @In
    EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(ScenarioRootManagementSystem.class);

    @ReceiveEvent
    public void onEventTrigger(EventTriggerEvent event, EntityRef entity, TriggerActionListComponent actions) {
        //Check Condition
        for (EntityRef c : entity.getComponent(TriggerConditionListComponent.class).conditions) {
            ConditionalCheckEvent cond = new ConditionalCheckEvent(event.informationEntity);
            c.send(cond);
            if (!cond.getResult()) {
                return; //Break check if any conditional is registered as false
            }
        }
        //Send to actions
        for (EntityRef a : actions.actions) {
            //Send new event in case eventually a new event needs to be made in which triggers and actions need different data
            a.send(new EventTriggerEvent(event.informationEntity));
        }
    }


    @ReceiveEvent
    public void onPlayerRespawnScenarioEvent(PlayerRespawnScenarioEvent event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondaryRespawnComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getSpawningEntity();
        EntityRef passEntity = entityManager.create(triggerEntity);
        entityList.forEach(e -> e.getOwner().send(new EventTriggerEvent(passEntity)));
    }

    @ReceiveEvent
    public void onPlayerSpawnScenarioEvent(PlayerSpawnScenarioEvent event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondarySpawnComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getSpawningEntity();
        EntityRef passEntity = entityManager.create(triggerEntity);
        entityList.forEach(e -> e.getOwner().send(new EventTriggerEvent(passEntity)));
    }

    @ReceiveEvent
    public void onDoDestroyScenarioEvent(DoDestroyScenarioEvent event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondaryBlockDestroyComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getInstigator();
        InfoDestroyedBlockComponent destroyed = new InfoDestroyedBlockComponent();
        destroyed.damageType = event.getDamageType();
        destroyed.destroyedBlock = event.getDestroyed();
        destroyed.directCause = event.getDirectCause();
        EntityRef passEntity = entityManager.create(triggerEntity, destroyed);
        entityList.forEach(e -> e.getOwner().send(new EventTriggerEvent(passEntity)));
    }

    @ReceiveEvent
    public void onPlayerEnterRegionEvent(PlayerEnterRegionEvent event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondaryEnterRegionComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getTriggerEntity();
        InfoTriggerRegionComponent triggerRegion = new InfoTriggerRegionComponent();
        triggerRegion.region = event.getRegion();
        EntityRef passEntity = entityManager.create(triggerEntity, triggerRegion);
        entityList.forEach(e -> {
            EvaluateRegionEvent reg = new EvaluateRegionEvent(passEntity);
            e.getComponent(ScenarioArgumentContainerComponent.class).arguments.get("region").send(reg);
            if (reg.getResult().equals(event.getRegion())) {
                e.getOwner().send(new EventTriggerEvent(passEntity));
            }
        });
    }

    @ReceiveEvent
    public void onPlayerLeaveRegionEvent(PlayerLeaveRegionEvent event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondaryLeaveRegionComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getTriggerEntity();
        InfoTriggerRegionComponent triggerRegion = new InfoTriggerRegionComponent();
        triggerRegion.region = event.getRegion();
        EntityRef passEntity = entityManager.create(triggerEntity, triggerRegion);
        entityList.forEach(e -> {
            EvaluateRegionEvent reg = new EvaluateRegionEvent(passEntity);
            e.getComponent(ScenarioArgumentContainerComponent.class).arguments.get("region").send(reg);
            if (reg.getResult().equals(event.getRegion())) {
                e.getOwner().send(new EventTriggerEvent(passEntity));
            }
        });
    }
}
