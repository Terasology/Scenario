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
import org.terasology.scenario.components.ExpandedComponent;
import org.terasology.scenario.components.ScenarioAttachedEntityComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.ScenarioHubToolUpdateComponent;
import org.terasology.scenario.components.TriggerActionListComponent;
import org.terasology.scenario.components.TriggerConditionListComponent;
import org.terasology.scenario.components.TriggerEventListComponent;
import org.terasology.scenario.components.TriggerNameComponent;
import org.terasology.scenario.components.actions.ActionComponent;
import org.terasology.scenario.components.conditionals.ConditionalComponent;
import org.terasology.scenario.components.events.EventComponent;
import org.terasology.scenario.internal.events.ConvertIntoEntityEvent;
import org.terasology.scenario.internal.events.LogicTreeAddActionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddConditionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddEventEvent;
import org.terasology.scenario.internal.events.LogicTreeAddTriggerEvent;
import org.terasology.scenario.internal.events.LogicTreeDeleteEvent;
import org.terasology.scenario.internal.events.LogicTreeMoveEntityEvent;
import org.terasology.scenario.internal.events.ReplaceEntityEvent;
import org.terasology.scenario.internal.events.ReplaceEntityFromConstructionStringsEvent;
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


    private EntityRef scenarioEntity;

    @Override
    public void postBegin() {
        Iterable<EntityRef> scenario = entityManager.getEntitiesWith(ScenarioComponent.class); // Checks for existing Scenario

        if (!scenario.iterator().hasNext()) { //No scenario exists yet
            scenarioEntity = entityManager.create(assetManager.getAsset("scenario:scenarioEntity", Prefab.class).get());
        }
        else {
            scenarioEntity = scenario.iterator().next();
        }
    }

    /**
     * Adding event, attaches to the scenarioComponent.actions in the Scenario root and then adds a new empty list
     * for eventually adding actions to that event. Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeAddEventEvent(LogicTreeAddEventEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        TriggerEventListComponent events = event.getTriggerEntity().getComponent(TriggerEventListComponent.class);

        EntityRef newEventEntity = entityManager.create(assetManager.getAsset("scenario:onPlayerSpawnEvent", Prefab.class).get());

        ArgumentParser argParser = new ArgumentParser();
        argParser.setAssetManager(assetManager);
        argParser.setBlockManager(blockManager);
        argParser.setEntityManager(entityManager);



        argParser.parseDefaults(newEventEntity);
        newEventEntity.setOwner(event.getTriggerEntity());
        events.events.add(newEventEntity);
        event.getTriggerEntity().saveComponent(events);
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForEvent);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ExpandedComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).addedEntity = newEventEntity;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    /**
     * Adding action, attaches to the ActionListComponent in the event entity.
     * Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeAddActionEvent(LogicTreeAddActionEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        logger.info("add action");
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
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForAction);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ExpandedComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).addedEntity = newActionEntity;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }


    /**
     * Adding condition, attaches to the scenarioComponent.actions in the Scenario root and then adds a new empty list
     * for eventually adding actions to that event. Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeAddConditionEvent(LogicTreeAddConditionEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        TriggerConditionListComponent conditions = event.getTriggerEntity().getComponent(TriggerConditionListComponent.class);
        //Sets up basic action as a give block component
        EntityRef newCondEntity = entityManager.create(assetManager.getAsset("scenario:blockConditional", Prefab.class).get());

        ArgumentParser argParser = new ArgumentParser();
        argParser.setAssetManager(assetManager);
        argParser.setBlockManager(blockManager);
        argParser.setEntityManager(entityManager);



        argParser.parseDefaults(newCondEntity);
        newCondEntity.setOwner(event.getTriggerEntity());
        conditions.conditions.add(newCondEntity);
        event.getTriggerEntity().saveComponent(conditions);
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForCondition);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ExpandedComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).addedEntity = newCondEntity;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }


    @ReceiveEvent
    public void onLogicTreeAddTriggerEvent(LogicTreeAddTriggerEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        EntityRef trigger = entityManager.create(assetManager.getAsset("Scenario:trigger", Prefab.class).get());
        TriggerEventListComponent events = trigger.getComponent(TriggerEventListComponent.class);
        trigger.saveComponent(events);
        TriggerConditionListComponent conds = trigger.getComponent(TriggerConditionListComponent.class);
        trigger.saveComponent(conds);
        TriggerActionListComponent actions = trigger.getComponent(TriggerActionListComponent.class);
        trigger.saveComponent(actions);

        TriggerNameComponent temp = trigger.getComponent(TriggerNameComponent.class);

        temp.entityForEvent = entityManager.create(assetManager.getAsset("scenario:emptyNetworkEntity", Prefab.class).get());
        temp.entityForCondition = entityManager.create(assetManager.getAsset("scenario:emptyNetworkEntity", Prefab.class).get());
        temp.entityForAction = entityManager.create(assetManager.getAsset("scenario:emptyNetworkEntity", Prefab.class).get());
        trigger.saveComponent(temp);

        scenarioEntity.getComponent(ScenarioComponent.class).triggerEntities.add(trigger);
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(entity);
            event.getHubScreen().getComponent(ExpandedComponent.class).expandedList.add(trigger);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ExpandedComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    /**
     * Checks if the deleted entity is an event or action and then removes and saves the correct entities.
     * Event just needs to update scenario root, action needs to update both scenario and the event it is attached to
     * Updates the hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeDeleteEvent(LogicTreeDeleteEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        if (event.getDeleteFromEntity().hasComponent(TriggerNameComponent.class)) { //Must be event/cond/action
            if (event.getDeleteEntity().hasComponent(EventComponent.class)) { //Event
                TriggerEventListComponent events = event.getDeleteFromEntity().getComponent(TriggerEventListComponent.class);
                events.events.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(events);
                event.getDeleteEntity().destroy();
            }
            else if (event.getDeleteEntity().hasComponent(ConditionalComponent.class)) { //Condition
                TriggerConditionListComponent conds = event.getDeleteFromEntity().getComponent(TriggerConditionListComponent.class);
                conds.conditions.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(conds);
                event.getDeleteEntity().destroy();
            }
            else if (event.getDeleteEntity().hasComponent(ActionComponent.class)) { //Action
                TriggerActionListComponent actions = event.getDeleteFromEntity().getComponent(TriggerActionListComponent.class);
                actions.actions.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(actions);
                event.getDeleteEntity().destroy();
            }
            entity.saveComponent(component);
        }
        else { //Must be a trigger, not an event/action/conditional
            scenarioEntity.getComponent(ScenarioComponent.class).triggerEntities.remove(event.getDeleteEntity());
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
            event.getDeleteEntity().destroy();
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    @ReceiveEvent
    public void onLogicTreeMoveEntityEvent(LogicTreeMoveEntityEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
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
                list = scenarioEntity.getComponent(ScenarioComponent.class).triggerEntities;
                break;
            default:
                for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
                    e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
                    e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
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
                for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
                    e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
                    e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
                }
                return;
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }


    /**
     * This event should only ever be called for replacing an action/event/condtional with the same type
     */
    @ReceiveEvent
    public void onReplaceEntityEvent(ReplaceEntityEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        logger.info(entity.getOwner().toFullDescription());
        logger.info(entity.getOwner().getComponent(ScenarioAttachedEntityComponent.class).ent.toFullDescription());
        EntityRef owningTrigger = event.getReplaced().getOwner();
        if (event.getReplaced().hasComponent(ActionComponent.class)) {
            TriggerActionListComponent actions = owningTrigger.getComponent(TriggerActionListComponent.class);
            event.getReplacer().setOwner(owningTrigger);
            int index = actions.actions.indexOf(event.getReplaced());
            actions.actions.remove(event.getReplaced());
            actions.actions.add(index, event.getReplacer());
            owningTrigger.saveComponent(actions);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }
        else if (event.getReplaced().hasComponent(EventComponent.class)) {
            TriggerEventListComponent events = owningTrigger.getComponent(TriggerEventListComponent.class);
            event.getReplacer().setOwner(owningTrigger);
            int index = events.events.indexOf(event.getReplaced());
            events.events.remove(event.getReplaced());
            events.events.add(index, event.getReplacer());
            owningTrigger.saveComponent(events);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }
        else if (event.getReplaced().hasComponent(ConditionalComponent.class)) {
            TriggerConditionListComponent conds = owningTrigger.getComponent(TriggerConditionListComponent.class);
            event.getReplacer().setOwner(owningTrigger);
            int index = conds.conditions.indexOf(event.getReplaced());
            conds.conditions.remove(event.getReplaced());
            conds.conditions.add(index, event.getReplacer());
            owningTrigger.saveComponent(conds);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }


        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }
    }

    @ReceiveEvent
    public void onReplaceEntityWithPrefabEvent(ReplaceEntityFromConstructionStringsEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        ArgumentParser argumentParser = new ArgumentParser();
        argumentParser.setBlockManager(blockManager);
        argumentParser.setAssetManager(assetManager);
        argumentParser.setEntityManager(entityManager);
        ConvertIntoEntityEvent conversionEvent = new ConvertIntoEntityEvent(event.getConversions());
        entity.send(conversionEvent);
        EntityRef newEntity = conversionEvent.getReturnEntity();
        EntityRef owningTrigger = event.getReplaced().getOwner();
        if (event.getReplaced().hasComponent(ActionComponent.class)) {
            TriggerActionListComponent actions = owningTrigger.getComponent(TriggerActionListComponent.class);
            newEntity.setOwner(owningTrigger);
            int index = actions.actions.indexOf(event.getReplaced());
            actions.actions.remove(event.getReplaced());
            actions.actions.add(index, newEntity);
            owningTrigger.saveComponent(actions);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }
        else if (event.getReplaced().hasComponent(EventComponent.class)) {
            TriggerEventListComponent events = owningTrigger.getComponent(TriggerEventListComponent.class);
            newEntity.setOwner(owningTrigger);
            int index = events.events.indexOf(event.getReplaced());
            events.events.remove(event.getReplaced());
            events.events.add(index, newEntity);
            owningTrigger.saveComponent(events);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }
        else if (event.getReplaced().hasComponent(ConditionalComponent.class)) {
            TriggerConditionListComponent conds = owningTrigger.getComponent(TriggerConditionListComponent.class);
            newEntity.setOwner(owningTrigger);
            int index = conds.conditions.indexOf(event.getReplaced());
            conds.conditions.remove(event.getReplaced());
            conds.conditions.add(index, newEntity);
            owningTrigger.saveComponent(conds);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }


        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.getComponent(ScenarioHubToolUpdateComponent.class).dirtyLogic = true;
            e.saveComponent(e.getComponent(ScenarioHubToolUpdateComponent.class));
        }

    }


}
