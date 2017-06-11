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
import org.terasology.scenario.components.ActionComponent;
import org.terasology.scenario.components.ActionListComponent;
import org.terasology.scenario.components.EventNameComponent;
import org.terasology.scenario.components.EventTypeComponent;
import org.terasology.scenario.components.ExpandedComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.internal.events.LogicTreeAddActionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddEventEvent;
import org.terasology.scenario.internal.events.LogicTreeDeleteEvent;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;

/**
 * The system that handles all of the events for the entity version of the tree structure.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class EntityTreeSystem extends BaseComponentSystem{
    private static final Logger logger = LoggerFactory.getLogger(EntityTreeSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockManager;

    /**
     * Adding event, attaches to the scenarioComponent.actions in the Scenario root and then adds a new empty list
     * for eventually adding actions to that event. Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeAddEventEvent(LogicTreeAddEventEvent event, EntityRef entity, ScenarioComponent component) {
        EventNameComponent eventName = new EventNameComponent();
        //ExpandedComponent exp = new ExpandedComponent();
        EventTypeComponent type = new EventTypeComponent();
        type.type = EventTypeComponent.eventType.PLAYER_SPAWN;
        eventName.name = event.getEventName();
        EntityRef newEventEntity = entityManager.create(new ActionListComponent(), eventName, type);
        ActionListComponent actionsList = newEventEntity.getComponent(ActionListComponent.class);
        actionsList.actions = new ArrayList<EntityRef>();
        newEventEntity.saveComponent(actionsList);

        component.triggerEntities.add(newEventEntity);
        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(entity);
            event.getHubScreen().getEntity().saveComponent(event.getHubScreen().getEntity().getComponent(ExpandedComponent.class));
            event.getHubScreen().updateTree(entity);
        }
    }

    /**
     * Adding action, attaches to the ActionListComponent in the event entity.
     * Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeAddActionEvent(LogicTreeAddActionEvent event, EntityRef entity, ScenarioComponent component) {
        ActionComponent actionName = new ActionComponent();
        actionName.type = event.getActionType();

        //REMOVE THIS ONCE EDITABLE FROM CONTEXT MENU
        actionName.itemIdName = blockManager.getBlock(actionName.itemId).getDisplayName();
        //END REMOVE

        EntityRef newActionEntity = entityManager.create(actionName);
        ActionListComponent actionsList = event.getEventEntity().getComponent(ActionListComponent.class);
        actionsList.actions.add(newActionEntity);
        event.getEventEntity().saveComponent(actionsList);

        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getEventEntity());
            event.getHubScreen().getEntity().saveComponent(event.getHubScreen().getEntity().getComponent(ExpandedComponent.class));
            event.getHubScreen().updateTree(entity);
        }
    }

    /**
     * Checks if the deleted entity is an event or action and then removes and saves the correct entities.
     * Event just needs to update scenario root, action needs to update both scenario and the event it is attached to
     * Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeDeleteEvent(LogicTreeDeleteEvent event, EntityRef entity, ScenarioComponent component) {
        if (event.getDeleteFromEntity().hasComponent(ActionListComponent.class)) { //Must be an action, not an event
            ActionListComponent actionList = event.getDeleteFromEntity().getComponent(ActionListComponent.class);
            actionList.actions.remove(event.getDeleteEntity());
            event.getDeleteFromEntity().saveComponent(actionList);
            entity.saveComponent(component);
        }
        else {
            component.triggerEntities.remove(event.getDeleteEntity());
            entity.saveComponent(component);
        }

        if (event.getHubScreen() != null) {
            event.getHubScreen().updateTree(entity);
        }
    }
}
