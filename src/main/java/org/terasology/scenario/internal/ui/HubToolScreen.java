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
package org.terasology.scenario.internal.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UIBox;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.treeView.Tree;
import org.terasology.scenario.components.ExpandedComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.TriggerActionListComponent;
import org.terasology.scenario.components.TriggerConditionListComponent;
import org.terasology.scenario.components.TriggerEventListComponent;
import org.terasology.scenario.components.TriggerNameComponent;
import org.terasology.scenario.internal.events.LogicTreeAddActionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddConditionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddEventEvent;
import org.terasology.scenario.internal.events.LogicTreeAddTriggerEvent;
import org.terasology.scenario.internal.events.LogicTreeDeleteEvent;
import org.terasology.scenario.internal.utilities.ArgumentParser;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HubToolScreen extends BaseInteractionScreen {
    private UIBox overviewBox;
    private UIBox logicBox;
    private UIBox regionBox;
    private UIButton overviewButton;
    private UIButton logicButton;
    private UIButton regionsButton;

    private UIButton addEventButton;
    private UIButton addActionButton;
    private UIButton deleteButton;

    private EntityRef scenarioEntity;

    private LogicTreeView treeView;

    private ArgumentParser parser;

    private EntityRef addedEntity;
    private LogicTree newAddedEntityTree;

    @In
    private AssetManager assetManager;

    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockManager;

    private Logger logger = LoggerFactory.getLogger(HubToolScreen.class);


    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {

    }

    public EntityRef getEntity() {
        return getInteractionTarget();
    }

    public EntityRef getScenarioEntity() {
        return scenarioEntity;
    }

    @Override
    public void initialise() {
        parser = new ArgumentParser();
        parser.setBlockManager(blockManager);
        parser.setEntityManager(entityManager);
        parser.setAssetManager(assetManager);
        overviewBox = find("overviewBox", UIBox.class);
        logicBox = find("logicBox", UIBox.class);
        regionBox = find("regionBox", UIBox.class);
        overviewButton = find("Overview", UIButton.class);
        logicButton = find("Logic", UIButton.class);
        regionsButton = find("Regions", UIButton.class);

        addEventButton = find("addEventButton", UIButton.class);
        addActionButton = find("addActionButton", UIButton.class);
        deleteButton = find("deleteButton", UIButton.class);

        treeView = find("logicTree", LogicTreeView.class);

        ExpandedComponent exp = getInteractionTarget().getComponent(ExpandedComponent.class);

        if (exp.expandedList == null) {
            exp.expandedList = new HashSet<>();
            getInteractionTarget().saveComponent(exp);
        }

        if (overviewButton != null) {
            overviewButton.subscribe(button -> {
                overviewBox.setVisible(true);
                logicBox.setVisible(false);
                regionBox.setVisible(false);
            });
        }

        if (logicButton != null) {
            logicButton.subscribe(button -> {
                overviewBox.setVisible(false);
                logicBox.setVisible(true);
                regionBox.setVisible(false);
            });
        }

        if (regionsButton != null) {
            regionsButton.subscribe(button -> {
                overviewBox.setVisible(false);
                logicBox.setVisible(false);
                regionBox.setVisible(true);
            });
        }

        if (addEventButton != null) {
            addEventButton.subscribe(this::onAddEventButton);
            addEventButton.bindEnabled(
                    new ReadOnlyBinding<Boolean>() {
                        @Override
                        public Boolean get() {
                            return checkCanAddEvent();
                        }
                    }
            );
        }

        if (addActionButton != null) {
            addActionButton.subscribe(this::onAddActionButton);
            addActionButton.bindEnabled(
                    new ReadOnlyBinding<Boolean>() {
                        @Override
                        public Boolean get() {
                            return checkCanAddAction();
                        }
                    }
            );
        }

        if (deleteButton != null) {
            deleteButton.subscribe(this::onDeleteButton);
            deleteButton.bindEnabled(
                    new ReadOnlyBinding<Boolean>() {
                        @Override
                        public Boolean get() {
                            return checkCanDelete();
                        }
                    }
            );
        }


        if (treeView != null){
            treeView.expandedList = new HashSet<>();
            treeView.subscribeNodeDoubleClick((event, node) -> {
                if (canEdit((LogicTree)node)) {
                    pushEditScreen((LogicTree) node);
                }
            });
            treeView.setContextMenuTreeProducer(node -> {
                LogicTreeMenuTreeBuilder logicTreeMenuTreeBuilder = new LogicTreeMenuTreeBuilder();
                logicTreeMenuTreeBuilder.setManager(getManager());
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_DELETE, this::delete);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_EVENT, this::addEvent);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_ACTION, this::addAction);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_CONDITIONAL, this::addCondition);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_TRIGGER, this::addTrigger);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_EDIT, this::pushEditScreen);
                logicTreeMenuTreeBuilder.subscribeAddContextMenu(n -> {
                    getEditor().fireUpdateListeners();
                });

                return logicTreeMenuTreeBuilder.createPrimaryContextMenu(node);
            });

            Iterable<EntityRef> scenario = entityManager.getEntitiesWith(ScenarioComponent.class); // Checks for existing Scenario

            if (scenario.iterator().hasNext()) { //If scenario exists
                EntityRef main = scenario.iterator().next();
                if (scenarioEntity == null || !scenarioEntity.equals(main)) {
                    scenarioEntity = main;
                }
                LogicTree tempTree = constructTree(main);
                if (tempTree != null) {
                    treeView.setModel(tempTree);
                }
            }
            else { //Create a new scenario if none exists
                ScenarioComponent tempComponent = new ScenarioComponent();
                scenarioEntity = entityManager.create(tempComponent);
            }

            ScenarioComponent tempScenComponent = scenarioEntity.getComponent(ScenarioComponent.class);
            if (tempScenComponent.triggerEntities == null) {  //Makes sure list isn't null, causes problems when building treeView
                tempScenComponent.triggerEntities = new ArrayList<>();
                scenarioEntity.saveComponent(tempScenComponent);
            }

            treeView.setEditor(getManager());
            treeView.setAssetManager(assetManager);
        }
    }

    private boolean canEdit(LogicTree node) {
        if (node.getValue().getValueType() == LogicTreeValue.Type.CONDITIONAL ||
                node.getValue().getValueType() == LogicTreeValue.Type.EVENT ||
                node.getValue().getValueType() == LogicTreeValue.Type.ACTION) {
            return true;
        }
        else {
            return false;
        }


    }

    private void pushEditScreen(LogicTree node) {
        EditLogicScreen editLogic = getManager().pushScreen(EditLogicScreen.ASSET_URI, EditLogicScreen.class);
        editLogic.setEntities(scenarioEntity, node.getValue().getEntity(), node.getValue().getValueType(), this, parser);
    }

    /**
     * Function for when button is triggered to add an event.
     */
    private void onAddEventButton(UIWidget button) {
        onAddEvent();
    }

    /**
     * Function for when context is hit to add an event.
     */
    private void addEvent(LogicTree node) {
        onAddEvent();
    }

    /**
     * Function to actually add an event to the selected index. Both button and context act the same currently.
     */
    private void onAddEvent() {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        scenarioEntity.send(new LogicTreeAddEventEvent(this, tree.getValue().getEntity()));
    }

    /**
     * Identification on if an event can be added from the selected index, currently it's if scenario root is selected
     * @return If add event button should be allowed.
     */
    private boolean checkCanAddEvent() {
        Integer selectedIndex = treeView.getSelectedIndex();
        if (selectedIndex == null) {
            return false;
        }

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        LogicTreeValue value = tree.getValue();
        return value.getValueType() == LogicTreeValue.Type.TRIGGER;
    }

    /**
     * Action, conditional, trigger and delete follow same as the above detailed event functions.
     */
    private void onAddActionButton(UIWidget button) {
        onAddAction();
    }

    private void addAction(LogicTree node) {
        onAddAction();
    }

    private void onAddAction() {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        scenarioEntity.send(new LogicTreeAddActionEvent(this, tree.getValue().getEntity()));
    }

    private boolean checkCanAddAction() {
        Integer selectedIndex = treeView.getSelectedIndex();
        if (selectedIndex == null) {
            return false;
        }

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        LogicTreeValue value = tree.getValue();
        return value.getValueType() == LogicTreeValue.Type.TRIGGER;
    }

    private void addCondition(LogicTree node) {
        onAddCondition();
    }

    private void onAddCondition() {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        scenarioEntity.send(new LogicTreeAddConditionEvent(this, tree.getValue().getEntity()));
    }

    private void addTrigger(LogicTree node) {
        onAddTrigger();
    }

    private void onAddTrigger() {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        scenarioEntity.send(new LogicTreeAddTriggerEvent(this));
    }

    private void onDeleteButton(UIWidget button) {
        onDelete();
    }

    private void delete(LogicTree node) {
        onDelete();
    }

    private void onDelete() {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        scenarioEntity.send(new LogicTreeDeleteEvent(tree.getValue().getEntity(), tree.getParent().getValue().getEntity(), this));
    }

    private boolean checkCanDelete() {
        Integer selectedIndex = treeView.getSelectedIndex();
        if (selectedIndex == null) {
            return false;
        }

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        return !tree.isRoot();
    }

    private LogicTreeView getEditor() {
        return this.treeView;
    }

    /**
     * When the hub tool screen is opened then rebuild the tree. Eventually the scenario structure will include a
     * clean/dirty design to prevent having to rebuild the entire tree every time.
     */
    @Override
    public void onOpened() {
        super.onOpened();
        Iterable<EntityRef> scenario = entityManager.getEntitiesWith(ScenarioComponent.class);

        if (scenario.iterator().hasNext()) {
            EntityRef main = scenario.iterator().next();
            if (scenarioEntity == null || !scenarioEntity.equals(main)) {
                scenarioEntity = main;
            }
            LogicTree tempTree = constructTree(main);
            if (tempTree != null) {
                treeView.setModel(tempTree);
            }
        }
        else {
            ScenarioComponent tempComponent = new ScenarioComponent();
            scenarioEntity = entityManager.create(tempComponent);
        }

        ScenarioComponent tempScenComponent = scenarioEntity.getComponent(ScenarioComponent.class);
        if (tempScenComponent.triggerEntities == null) {
            tempScenComponent.triggerEntities = new ArrayList<>();
            scenarioEntity.saveComponent(tempScenComponent);
        }
    }

    /**
     * Function for updating the currently displayed tree with a new root for the scenario tree.
     *
     * @param newScenario must have a ScenarioComponent in order to build the tree detailed from it.
     *                automatically open the edit screen
     */
    public void updateTree(EntityRef newScenario) {
        if (newScenario.getComponent(ScenarioComponent.class) != null) {
            if (!scenarioEntity.equals(newScenario)) {
                scenarioEntity.destroy();
            }
            scenarioEntity = newScenario;
            LogicTree tempTree = constructTree(scenarioEntity);
            if (tempTree != null) {
                treeView.setModel(tempTree);
            }
            if (newAddedEntityTree != null) {
                if (canEdit(newAddedEntityTree)) {
                    pushEditScreen(newAddedEntityTree);
                    newAddedEntityTree = null;
                }
                else {
                    newAddedEntityTree = null;
                }
            }
        }
    }

    public void setAddedEntity(EntityRef addedEntity) {
        this.addedEntity = addedEntity;

    }

    /**
     * Constructs the treeView version of the tree detailed in the entity/component tree structure for logic
     * @param entity The root entity (EntityRef with a ScenarioComponent)
     * @return LogicTree built from the given entityRef.
     */
    public LogicTree constructTree(EntityRef entity) {
        if (!entity.hasComponent(ScenarioComponent.class)) {
            return null;
        }
        ScenarioComponent scenario = entity.getComponent(ScenarioComponent.class);
        LogicTreeView tempTreeView = new LogicTreeView();
        LogicTree returnTree = new LogicTree(new LogicTreeValue("Scenario", assetManager.getAsset("Scenario:scenarioText", Texture.class).get(),  LogicTreeValue.Type.SCENARIO, entity, parser), this);
        tempTreeView.setModel(returnTree.getRoot());
        returnTree.setExpandedNoEntity(true);

        if (scenario.triggerEntities != null) {
            List<EntityRef> triggers = scenario.triggerEntities;
            for (EntityRef t : triggers) {
                TriggerNameComponent name = t.getComponent(TriggerNameComponent.class);
                TriggerEventListComponent events = t.getComponent(TriggerEventListComponent.class);
                TriggerConditionListComponent conditionals = t.getComponent(TriggerConditionListComponent.class);
                TriggerActionListComponent actions = t.getComponent(TriggerActionListComponent.class);
                LogicTreeValue value;

                //Assumes all components are non-null, if one isn't then it's a bad trigger entity anyways and will cause problems elsewhere
                value = new LogicTreeValue(name.name, assetManager.getAsset("Scenario:triggerText", Texture.class).get(), LogicTreeValue.Type.TRIGGER, t, parser);
                LogicTree event = new LogicTree(new LogicTreeValue("Events", assetManager.getAsset("Scenario:eventText", Texture.class).get(), LogicTreeValue.Type.EVENT_NAME, t, parser), this);
                if (getInteractionTarget().getComponent(ExpandedComponent.class).expandedList.contains(t.getComponent(TriggerNameComponent.class).entityForEvent)) {
                    event.setExpandedNoEntity(true);
                }

                LogicTree condition = new LogicTree(new LogicTreeValue("Conditionals", assetManager.getAsset("Scenario:conditionalText", Texture.class).get(), LogicTreeValue.Type.CONDITIONAL_NAME, t, parser), this);
                if (getInteractionTarget().getComponent(ExpandedComponent.class).expandedList.contains(t.getComponent(TriggerNameComponent.class).entityForCondition)) {
                    condition.setExpandedNoEntity(true);
                }

                LogicTree action = new LogicTree(new LogicTreeValue("Actions", assetManager.getAsset("Scenario:actionText", Texture.class).get(), LogicTreeValue.Type.ACTION_NAME, t, parser), this);
                if (getInteractionTarget().getComponent(ExpandedComponent.class).expandedList.contains(t.getComponent(TriggerNameComponent.class).entityForAction)) {
                    action.setExpandedNoEntity(true);
                }

                LogicTree tempTriggerTree = new LogicTree(value, this);
                tempTriggerTree.addChild(event);
                tempTriggerTree.addChild(condition);
                tempTriggerTree.addChild(action);

                for (EntityRef e : events.events) {
                    if (addedEntity != null && addedEntity.equals(e)) {
                        addedEntity = null;
                        newAddedEntityTree = new LogicTree(new LogicTreeValue(assetManager.getAsset("Scenario:eventText", Texture.class).get(), LogicTreeValue.Type.EVENT, e, parser), this);
                        event.addChild(newAddedEntityTree);
                    }
                    else {
                        event.addChild(new LogicTreeValue(assetManager.getAsset("Scenario:eventText", Texture.class).get(), LogicTreeValue.Type.EVENT, e, parser));
                    }
                }
                for (EntityRef c : conditionals.conditions) {
                    if (addedEntity != null && addedEntity.equals(c)) {
                        addedEntity = null;
                        newAddedEntityTree = new LogicTree(new LogicTreeValue(assetManager.getAsset("Scenario:conditionalText", Texture.class).get(), LogicTreeValue.Type.CONDITIONAL, c, parser), this);
                        condition.addChild(newAddedEntityTree);
                    }
                    else {
                        condition.addChild(new LogicTreeValue(assetManager.getAsset("Scenario:conditionalText", Texture.class).get(), LogicTreeValue.Type.CONDITIONAL, c, parser));
                    }
                }
                for (EntityRef a : actions.actions) {
                    if (addedEntity != null && addedEntity.equals(a)) {
                        addedEntity = null;
                        newAddedEntityTree = new LogicTree(new LogicTreeValue(assetManager.getAsset("Scenario:actionText", Texture.class).get(), LogicTreeValue.Type.ACTION, a, parser), this);
                        action.addChild(newAddedEntityTree);
                    }
                    else {
                        action.addChild(new LogicTreeValue(assetManager.getAsset("Scenario:actionText", Texture.class).get(), LogicTreeValue.Type.ACTION, a, parser));
                    }
                }
                returnTree.addChild(tempTriggerTree);
                tempTriggerTree.setExpandedNoEntity(getInteractionTarget().getComponent(ExpandedComponent.class).expandedList.contains(t));

            }
        }
        return returnTree;
    }
}
