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
import org.terasology.registry.In;
import org.terasology.scenario.components.actions.ActionComponent;
import org.terasology.scenario.components.actions.ActionHeadComponent;
import org.terasology.scenario.components.information.InformationEnums;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.internal.events.EventTriggerEvent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * System that responds and triggers the actions of a logic event
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ActionEventSystem extends BaseComponentSystem {
    @In
    EntityManager entityManager;

    @In
    BlockManager blockManager;

    private BlockItemFactory blockItemFactory;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActionEventSystem.class);

    @Override
    public void initialise() {
        super.initialise();
        blockItemFactory = new BlockItemFactory(entityManager);
    }

    @ReceiveEvent
    public void onEventTriggerEvent(EventTriggerEvent event, EntityRef entity, ActionHeadComponent action) {
        ActionComponent actualAction;
        Iterator<Component> components = action.action.iterateComponents().iterator();
        while (components.hasNext()){
            Component tempComp = components.next();
            if (tempComp instanceof ActionComponent) {
                actualAction = (ActionComponent)tempComp;
                Map<String, InformationEnums.DataTypes> satisfy = actualAction.neededTypes();
                Map<String, EntityRef> passVariables = new HashMap<>();
                for(Map.Entry<String, InformationEnums.DataTypes> v : satisfy.entrySet()) {
                    switch (v.getValue()) {
                        case PLAYER:
                            switch (actualAction.getVariable(v.getKey()).getComponent(PlayerComponent.class).type){
                                case TRIGGERING_PLAYER:
                                    PlayerComponent playerComp = new PlayerComponent();
                                    playerComp.value = event.triggeringEntity;
                                    passVariables.put(v.getKey(), entityManager.create(playerComp));
                                    break;
                                case TARGETTED_PLAYER:
                                    //Put targetted entity
                                    break;
                                default:
                                    break;
                            }
                        case BLOCK:
                            //Put block
                            break;
                        case INTEGER:
                            //Put Integer
                            break;
                        default:
                                break;
                    }
                }

                ((ActionComponent) tempComp).triggerAction(passVariables, entityManager);
                break;
            }
        }
    }
}
