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
import org.terasology.logic.inventory.events.GiveItemEvent;
import org.terasology.registry.In;
import org.terasology.scenario.components.ActionComponent;
import org.terasology.scenario.internal.events.EventTriggerEvent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemFactory;

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

    @Override
    public void initialise() {
        super.initialise();
        blockItemFactory = new BlockItemFactory(entityManager);
    }

    @ReceiveEvent
    public void onEventTriggerEvent(EventTriggerEvent event, EntityRef entity, ActionComponent action) {
        switch (action.type) {
            case GIVE_ITEM:
                EntityRef target = event.triggeringEntity;
                BlockFamily blockFamily = blockManager.getBlockFamily(blockManager.getBlock(action.itemId).getURI());
                EntityRef item = blockItemFactory.newInstance(blockFamily, action.numItems);

                GiveItemEvent giveItemEvent = new GiveItemEvent(event.triggeringEntity);
                item.send(giveItemEvent);
                if (!giveItemEvent.isHandled()) {
                    item.destroy();
                }

        }
    }
}
