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
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.scenario.components.ConditionComponent;
import org.terasology.scenario.components.EventNameComponent;
import org.terasology.scenario.components.ExpandedComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.TriggerActionListComponent;
import org.terasology.scenario.components.TriggerConditionListComponent;
import org.terasology.scenario.components.TriggerEventListComponent;
import org.terasology.scenario.components.TriggerNameComponent;
import org.terasology.scenario.components.actions.ActionComponent;
import org.terasology.scenario.components.actions.TextComponent;
import org.terasology.scenario.components.events.OnSpawnComponent;
import org.terasology.scenario.internal.events.LogicTreeAddActionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddConditionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddEventEvent;
import org.terasology.scenario.internal.events.LogicTreeAddTriggerEvent;
import org.terasology.scenario.internal.events.LogicTreeDeleteEvent;
import org.terasology.scenario.internal.events.LogicTreeMoveEntityEvent;
import org.terasology.scenario.internal.events.ReplaceEntityEvent;
import org.terasology.scenario.internal.utilities.ArgumentParser;
import org.terasology.world.block.BlockManager;

import java.util.List;

/**
 * The system that handles all of the events for the entity version of the tree structure.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class EntityTreeSystem extends BaseComponentSystem{
    private static final Logger logger = LoggerFactory.getLogger(EntityTreeSystem.class);

    @In
    private AssetManager assetManager;

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
        TriggerEventListComponent events = event.getTriggerEntity().getComponent(TriggerEventListComponent.class);
        OnSpawnComponent spawn = new OnSpawnComponent();
        EventNameComponent name = new EventNameComponent();
        EntityRef newEventEntity = entityManager.create(spawn, name);
        newEventEntity.setOwner(event.getTriggerEntity());
        events.events.add(newEventEntity);
        event.getTriggerEntity().saveComponent(events);
        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForEvent);
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
        TriggerActionListComponent actions = event.getTriggerEntity().getComponent(TriggerActionListComponent.class);
        //Sets up basic action as a give block component
        EntityRef newActionEntity = entityManager.create(assetManager.getAsset("scenario:givePlayerBlockAction", Prefab.class).get());

        ArgumentParser argParser = new ArgumentParser();
        argParser.setAssetManager(assetManager);
        argParser.setBlockManager(blockManager);
        argParser.setEntityManager(entityManager);



        argParser.parseDefaults(newActionEntity);
        newActionEntity.setOwner(event.getTriggerEntity());
        actions.actions.add(newActionEntity);
        event.getTriggerEntity().saveComponent(actions);
        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForAction);
            event.getHubScreen().getEntity().saveComponent(event.getHubScreen().getEntity().getComponent(ExpandedComponent.class));
            event.getHubScreen().updateTree(entity);
        }
    }


    /**
     * Adding condition, attaches to the scenarioComponent.actions in the Scenario root and then adds a new empty list
     * for eventually adding actions to that event. Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeAddConditionEvent(LogicTreeAddConditionEvent event, EntityRef entity, ScenarioComponent component) {
        TriggerConditionListComponent conditions = event.getTriggerEntity().getComponent(TriggerConditionListComponent.class);
        ConditionComponent cond = new ConditionComponent();
        cond.name = "New Condition";
        EntityRef newCondEntity = entityManager.create(cond);
        newCondEntity.setOwner(event.getTriggerEntity());
        conditions.conditions.add(newCondEntity);
        event.getTriggerEntity().saveComponent(conditions);
        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForCondition);
            event.getHubScreen().getEntity().saveComponent(event.getHubScreen().getEntity().getComponent(ExpandedComponent.class));
            event.getHubScreen().updateTree(entity);
        }
    }


    @ReceiveEvent
    public void onLogicTreeAddTriggerEvent(LogicTreeAddTriggerEvent event, EntityRef entity, ScenarioComponent component) {
        EntityRef trigger = entityManager.create(assetManager.getAsset("Scenario:trigger", Prefab.class).get());
        TriggerEventListComponent events = trigger.getComponent(TriggerEventListComponent.class);
        trigger.saveComponent(events);
        TriggerConditionListComponent conds = trigger.getComponent(TriggerConditionListComponent.class);
        trigger.saveComponent(conds);
        TriggerActionListComponent actions = trigger.getComponent(TriggerActionListComponent.class);
        trigger.saveComponent(actions);

        TriggerNameComponent temp = trigger.getComponent(TriggerNameComponent.class);

        temp.entityForEvent = entityManager.create();
        temp.entityForCondition = entityManager.create();
        temp.entityForAction = entityManager.create();
        trigger.saveComponent(temp);

        component.triggerEntities.add(trigger);
        entity.saveComponent(component);

        if (event.getHubScreen() != null) {
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(entity);
            event.getHubScreen().getEntity().getComponent(ExpandedComponent.class).expandedList.add(trigger);
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
        if (event.getDeleteFromEntity().hasComponent(TriggerNameComponent.class)) { //Must be event/cond/action
            if (event.getDeleteEntity().hasComponent(EventNameComponent.class)) { //Event
                TriggerEventListComponent events = event.getDeleteFromEntity().getComponent(TriggerEventListComponent.class);
                events.events.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(events);
                event.getDeleteEntity().destroy();
            }
            else if (event.getDeleteEntity().hasComponent(ConditionComponent.class)) { //Condition
                TriggerConditionListComponent conds = event.getDeleteFromEntity().getComponent(TriggerConditionListComponent.class);
                conds.conditions.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(conds);
                event.getDeleteEntity().destroy();
            }
            else if (event.getDeleteEntity().hasComponent(TextComponent.class)) { //Action
                TriggerActionListComponent actions = event.getDeleteFromEntity().getComponent(TriggerActionListComponent.class);
                actions.actions.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(actions);
                event.getDeleteEntity().destroy();
            }
            entity.saveComponent(component);
        }
        else { //Must be a trigger, not an event/action/conditional
            component.triggerEntities.remove(event.getDeleteEntity());
            entity.saveComponent(component);
            event.getDeleteEntity().destroy();
        }

        if (event.getHubScreen() != null) {
            event.getHubScreen().updateTree(entity);
        }
    }

    @ReceiveEvent
    public void onLogicTreeMoveEntityEvent(LogicTreeMoveEntityEvent event, EntityRef entity, ScenarioComponent component) {
        List<EntityRef> list;
        switch (event.getElementType()) {
            case EVENT:
                list = event.getTriggerEntity().getComponent(TriggerEventListComponent.class).events;
                break;
            case CONDITIONAL:
                list = event.getTriggerEntity().getComponent(TriggerConditionListComponent.class).conditions;
                break;
            case ACTION:
                list = event.getTriggerEntity().getComponent(TriggerActionListComponent.class).actions;
                break;
            case TRIGGER:
                list = component.triggerEntities;
                break;
            default:
                if (event.getHubScreen() != null) {
                    event.getHubScreen().updateTree(entity);
                }
                return;
        }
        int startIndex = list.indexOf(event.getMoveEntity());
        int endIndex = event.getIndex();
        if (startIndex < endIndex) {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex);
        }
        else {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex + 1);
        }

        switch (event.getElementType()) {
            case EVENT:
                event.getTriggerEntity().saveComponent(event.getTriggerEntity().getComponent(TriggerEventListComponent.class));
                entity.saveComponent(component);
                break;
            case CONDITIONAL:
                event.getTriggerEntity().saveComponent(event.getTriggerEntity().getComponent(TriggerConditionListComponent.class));
                entity.saveComponent(component);
                break;
            case ACTION:
                event.getTriggerEntity().saveComponent(event.getTriggerEntity().getComponent(TriggerActionListComponent.class));
                entity.saveComponent(component);
                break;
            case TRIGGER:
                entity.saveComponent(component);
                break;
            default:
                if (event.getHubScreen() != null) {
                    event.getHubScreen().updateTree(entity);
                }
                return;
        }

        if (event.getHubScreen() != null) {
            event.getHubScreen().updateTree(entity);
        }
    }


    /**
     * This event should only ever be called for replacing an action/event/condtional with the same type
     */
    @ReceiveEvent
    public void onReplaceEntityEvent(ReplaceEntityEvent event, EntityRef entity, ScenarioComponent component) {
        EntityRef owningTrigger = event.getReplaced().getOwner();
        if (event.getReplaced().hasComponent(ActionComponent.class)) {
            TriggerActionListComponent actions = owningTrigger.getComponent(TriggerActionListComponent.class);
            event.getReplacer().setOwner(owningTrigger);
            int index = actions.actions.indexOf(event.getReplaced());
            actions.actions.remove(event.getReplaced());
            actions.actions.add(index, event.getReplacer());
            owningTrigger.saveComponent(actions);
            entity.saveComponent(component);
        }


        if (event.getHubtool() != null) {
            event.getHubtool().updateTree(entity);
        }
    }


}
