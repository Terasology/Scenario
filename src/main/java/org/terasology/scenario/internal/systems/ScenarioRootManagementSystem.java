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
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.TriggerActionListComponent;
import org.terasology.scenario.components.TriggerConditionListComponent;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.events.*;
import org.terasology.scenario.components.events.triggerInformation.InfoDestroyedBlockComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoAddedBlockComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoTriggerRegionComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoTriggeringEntityComponent;
import org.terasology.scenario.internal.events.EventTriggerEvent;
import org.terasology.scenario.internal.events.evaluationEvents.ConditionalCheckEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateRegionEvent;
import org.terasology.scenario.internal.events.scenarioEvents.*;

/**
 * System that relays game events into scenario events and sends them using a filled up information entity that contains information of the trigger
 * which could include who the triggering entity or region is, or block details for breaking the block, etc
 *
 * First checks any conditionals with a {@link ConditionalCheckEvent} and if the conditional is satisfies it
 * Will send {@link EventTriggerEvent} to the attached list of actions, technically in the order of the actions on the hubtool
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ScenarioRootManagementSystem extends BaseComponentSystem {
    @In
    EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(ScenarioRootManagementSystem.class);



    @ReceiveEvent
    public void onEventTrigger(EventTriggerEvent event, EntityRef entity, TriggerActionListComponent actions) {
        //Check Condition
        for(EntityRef c : entity.getComponent(TriggerConditionListComponent.class).conditions) {
            ConditionalCheckEvent cond = new ConditionalCheckEvent(event.informationEntity);
            c.send(cond);
            if (!cond.getResult()){
                return; //Break check if any conditional is registered as false
            }
        }
        //Send to actions
        for(EntityRef a : actions.actions) {
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

    @ReceiveEvent
    public void onOnBlockAddedScenario(OnBlockItemAddedScenario event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondaryBlockAddComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getPlacedBlock();
        InfoAddedBlockComponent added = new InfoAddedBlockComponent();
        added.getPosition = event.getPosition();
        added.getPlacedBlock = event.getPlacedBlock();
        EntityRef passEntity = entityManager.create(triggerEntity, added);
        entityList.forEach(e -> e.getOwner().send(new EventTriggerEvent(passEntity)));
    }

}
