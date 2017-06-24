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

import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.events.GiveItemEvent;
import org.terasology.registry.In;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.actions.GiveBlockActionComponent;
import org.terasology.scenario.components.actions.LogInfoComponent;
import org.terasology.scenario.components.events.triggerInformation.TriggeringEntityComponent;
import org.terasology.scenario.components.information.BlockComponent;
import org.terasology.scenario.components.information.InformationEnums;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.internal.events.EventTriggerEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateIntEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateStringEvent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * System that responds and triggers the actions of a logic event
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ActionEventSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockManager;

    private BlockItemFactory blockItemFactory;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActionEventSystem.class);

    @Override
    public void initialise() {
        super.initialise();
        blockItemFactory = new BlockItemFactory(entityManager);
    }

    @ReceiveEvent //Give Block
    public void onEventTriggerEvent(EventTriggerEvent event, EntityRef entity, GiveBlockActionComponent action) {
        Map<String, EntityRef> variables = entity.getComponent(ArgumentContainerComponent.class).arguments;

        BlockFamily block = variables.get("block").getComponent(BlockComponent.class).value;
        EvaluateIntEvent intEvaluateEvent = new EvaluateIntEvent();
        variables.get("amount").send(intEvaluateEvent);
        int amount = intEvaluateEvent.getResult();

        EntityRef item = blockItemFactory.newInstance(block, amount);

        InformationEnums.PlayerType player = variables.get("player").getComponent(PlayerComponent.class).type;
        if (player == InformationEnums.PlayerType.TRIGGERING_PLAYER) {
            EntityRef giveEntity = event.informationEntity.getComponent(TriggeringEntityComponent.class).entity;
            GiveItemEvent giveItemEvent = new GiveItemEvent(giveEntity);
            item.send(giveItemEvent);
        }
    }

    @ReceiveEvent //Logger message
    public void onEventTriggerEvent(EventTriggerEvent event, EntityRef entity, LogInfoComponent action) {
        Map<String, EntityRef> variables = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateStringEvent stringEvaluateEvent = new EvaluateStringEvent();
        variables.get("text").send(stringEvaluateEvent);
        String out = stringEvaluateEvent.getResult();

        logger.info(out);
    }
}
