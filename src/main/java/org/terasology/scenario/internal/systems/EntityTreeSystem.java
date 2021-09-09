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
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.scenario.components.HubToolExpansionComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.ScenarioHubToolUpdateComponent;
import org.terasology.scenario.components.TriggerActionListComponent;
import org.terasology.scenario.components.TriggerConditionListComponent;
import org.terasology.scenario.components.TriggerEventListComponent;
import org.terasology.scenario.components.TriggerNameComponent;
import org.terasology.scenario.components.actions.ScenarioIndicatorActionComponent;
import org.terasology.scenario.components.conditionals.ScenarioIndicatorConditionalComponent;
import org.terasology.scenario.components.events.ScenarioIndicatorEventComponent;
import org.terasology.scenario.internal.events.ConvertIntoEntityEvent;
import org.terasology.scenario.internal.events.HubtoolRewriteLogicEvent;
import org.terasology.scenario.internal.events.LogicTreeAddActionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddConditionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddEventEvent;
import org.terasology.scenario.internal.events.LogicTreeAddTriggerEvent;
import org.terasology.scenario.internal.events.LogicTreeDeleteEvent;
import org.terasology.scenario.internal.events.LogicTreeMoveEntityEvent;
import org.terasology.scenario.internal.events.ReplaceEntityFromConstructionStringsEvent;
import org.terasology.scenario.internal.utilities.ArgumentParser;

import java.util.List;

/**
 * The system that handles all of the events for the entity version of the tree structure.
 *
 * Allows for clients to make request to the entity tree that is contained on the server's side.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class EntityTreeSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(EntityTreeSystem.class);

    @In
    private AssetManager assetManager;

    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockManager;

    @In
    private ArgumentParser argumentParser;

    private EntityRef scenarioEntity;

    /**
     * The whole scenario system relies on only one scenario entity existing
     */
    @Override
    public void postBegin() {
        Iterable<EntityRef> scenario = entityManager.getEntitiesWith(ScenarioComponent.class); // Checks for existing Scenario

        if (!scenario.iterator().hasNext()) { //No scenario exists yet
            scenarioEntity = entityManager.create(assetManager.getAsset("scenario:scenarioEntity", Prefab.class).get());
        } else {
            scenarioEntity = scenario.iterator().next();
        }
    }

    /**
     * Adding event, attaches to the scenarioComponent.actions in the Scenario root and then adds a new empty list for
     * eventually adding actions to that event. Updates the hub tool's screen if it was passed with the event. Does this
     * by indicating to all hubtools that it's logic is "dirty" and needs to be refreshed and telling the adding hubtool
     * what entity was just added so that it can prompt open the edit screen
     */
    @ReceiveEvent
    public void onLogicTreeAddEventEvent(LogicTreeAddEventEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        TriggerEventListComponent events = event.getTriggerEntity().getComponent(TriggerEventListComponent.class);

        EntityRef newEventEntity = entityManager.create(assetManager.getAsset("scenario:onPlayerSpawnEvent", Prefab.class).get());

        argumentParser.parseDefaults(newEventEntity);
        newEventEntity.setOwner(event.getTriggerEntity());
        events.events.add(newEventEntity);
        event.getTriggerEntity().saveComponent(events);
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForEvent);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(HubToolExpansionComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).addedEntity = newEventEntity;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }
    }

    /**
     * Adding action, attaches to the ActionListComponent in the event entity. Updates the hub tool's screen if it was
     * passed with the event. Does this by indicating to all hubtools that it's logic is "dirty" and needs to be
     * refreshed and telling the adding hubtool what entity was just added so that it can prompt open the edit screen
     */
    @ReceiveEvent
    public void onLogicTreeAddActionEvent(LogicTreeAddActionEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        TriggerActionListComponent actions = event.getTriggerEntity().getComponent(TriggerActionListComponent.class);
        //Sets up the default basic action as a give block component
        EntityRef newActionEntity = entityManager.create(assetManager.getAsset("scenario:givePlayerBlockAction", Prefab.class).get());

        argumentParser.parseDefaults(newActionEntity);
        newActionEntity.setOwner(event.getTriggerEntity());
        actions.actions.add(newActionEntity);
        event.getTriggerEntity().saveComponent(actions);
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForAction);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(HubToolExpansionComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).addedEntity = newActionEntity;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }
    }


    /**
     * Adding condition, attaches to the scenarioComponent.actions in the Scenario root and then adds a new empty list
     * for eventually adding actions to that event. Updates the hub tool's screen if it was passed with the event. Does
     * this by indicating to all hubtools that it's logic is "dirty" and needs to be refreshed and telling the adding
     * hubtool what entity was just added so that it can prompt open the edit screen
     */
    @ReceiveEvent
    public void onLogicTreeAddConditionEvent(LogicTreeAddConditionEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        TriggerConditionListComponent conditions = event.getTriggerEntity().getComponent(TriggerConditionListComponent.class);
        //Sets up basic action as a give block component
        EntityRef newCondEntity = entityManager.create(assetManager.getAsset("scenario:blockConditional", Prefab.class).get());

        argumentParser.parseDefaults(newCondEntity);
        newCondEntity.setOwner(event.getTriggerEntity());
        conditions.conditions.add(newCondEntity);
        event.getTriggerEntity().saveComponent(conditions);
        scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));

        if (event.getHubScreen() != null) {
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(event.getTriggerEntity());
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(event.getTriggerEntity().getComponent(TriggerNameComponent.class).entityForCondition);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(HubToolExpansionComponent.class));
            event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class).addedEntity = newCondEntity;
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }
    }

    /**
     * Adds a trigger entity to the trigger list of the scenario entity, tells all hubtools to redraw and adds the new
     * entity to the expansion list of the creating hubtool
     */
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
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(entity);
            event.getHubScreen().getComponent(HubToolExpansionComponent.class).expandedList.add(trigger);
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(HubToolExpansionComponent.class));
            event.getHubScreen().saveComponent(event.getHubScreen().getComponent(ScenarioHubToolUpdateComponent.class));
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }
    }

    /**
     * Checks if the deleted entity is an event or action and then removes and saves the correct entities. Event just
     * needs to update scenario root, action needs to update both scenario and the event it is attached to Updates the
     * hub tool's screen if it was passed with the event.
     */
    @ReceiveEvent
    public void onLogicTreeDeleteEvent(LogicTreeDeleteEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        if (event.getDeleteFromEntity().hasComponent(TriggerNameComponent.class)) { //Must be event/cond/action
            if (event.getDeleteEntity().hasComponent(ScenarioIndicatorEventComponent.class)) { //Event
                TriggerEventListComponent events = event.getDeleteFromEntity().getComponent(TriggerEventListComponent.class);
                events.events.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(events);
                event.getDeleteEntity().destroy();
            } else if (event.getDeleteEntity().hasComponent(ScenarioIndicatorConditionalComponent.class)) { //Condition
                TriggerConditionListComponent conds = event.getDeleteFromEntity().getComponent(TriggerConditionListComponent.class);
                conds.conditions.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(conds);
                event.getDeleteEntity().destroy();
            } else if (event.getDeleteEntity().hasComponent(ScenarioIndicatorActionComponent.class)) { //Action
                TriggerActionListComponent actions = event.getDeleteFromEntity().getComponent(TriggerActionListComponent.class);
                actions.actions.remove(event.getDeleteEntity());
                event.getDeleteFromEntity().saveComponent(actions);
                event.getDeleteEntity().destroy();
            }
            entity.saveComponent(component);
        } else { //Must be a trigger, not an event/action/conditional
            scenarioEntity.getComponent(ScenarioComponent.class).triggerEntities.remove(event.getDeleteEntity());
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
            event.getDeleteEntity().destroy();
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }
    }

    /**
     * Re-orders the tree based on the desired index and the starting index of a logic
     */
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
                    e.send(new HubtoolRewriteLogicEvent());
                }
                return;
        }
        int startIndex = list.indexOf(event.getMoveEntity());
        int endIndex = event.getIndex();
        if (startIndex < endIndex) {
            list.add(endIndex, list.get(startIndex));
            list.remove(startIndex);
        } else {
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
                    e.send(new HubtoolRewriteLogicEvent());
                }
                return;
        }

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }
    }

    /**
     * Takes the serialized list from a client and constructs it back into an entity and replaces the original entity in
     * the logic tree. Serialised using ConvertEntitySystem and converts back into entity using ConvertIntoEntitySystem
     */
    @ReceiveEvent
    public void onReplaceEntityWithPrefabEvent(ReplaceEntityFromConstructionStringsEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        ConvertIntoEntityEvent conversionEvent = new ConvertIntoEntityEvent(event.getConversions());
        entity.send(conversionEvent);
        EntityRef newEntity = conversionEvent.getReturnEntity();
        EntityRef owningTrigger = event.getReplaced().getOwner();
        if (event.getReplaced().hasComponent(ScenarioIndicatorActionComponent.class)) {
            TriggerActionListComponent actions = owningTrigger.getComponent(TriggerActionListComponent.class);
            newEntity.setOwner(owningTrigger);
            int index = actions.actions.indexOf(event.getReplaced());
            actions.actions.remove(event.getReplaced());
            actions.actions.add(index, newEntity);
            owningTrigger.saveComponent(actions);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        } else if (event.getReplaced().hasComponent(ScenarioIndicatorEventComponent.class)) {
            TriggerEventListComponent events = owningTrigger.getComponent(TriggerEventListComponent.class);
            newEntity.setOwner(owningTrigger);
            int index = events.events.indexOf(event.getReplaced());
            events.events.remove(event.getReplaced());
            events.events.add(index, newEntity);
            owningTrigger.saveComponent(events);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        } else if (event.getReplaced().hasComponent(ScenarioIndicatorConditionalComponent.class)) {
            TriggerConditionListComponent conds = owningTrigger.getComponent(TriggerConditionListComponent.class);
            newEntity.setOwner(owningTrigger);
            int index = conds.conditions.indexOf(event.getReplaced());
            conds.conditions.remove(event.getReplaced());
            conds.conditions.add(index, newEntity);
            owningTrigger.saveComponent(conds);
            scenarioEntity.saveComponent(scenarioEntity.getComponent(ScenarioComponent.class));
        }

        event.getReplaced().destroy();

        for (EntityRef e : entityManager.getEntitiesWith(ScenarioHubToolUpdateComponent.class)) {
            e.send(new HubtoolRewriteLogicEvent());
        }

    }
}
