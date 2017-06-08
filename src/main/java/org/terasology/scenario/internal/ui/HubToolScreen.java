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
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UIBox;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.treeView.Tree;
import org.terasology.scenario.components.ActionComponent;
import org.terasology.scenario.components.ActionListComponent;
import org.terasology.scenario.components.EventNameComponent;
import org.terasology.scenario.components.ExpandedComponent;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.internal.events.LogicTreeAddActionEvent;
import org.terasology.scenario.internal.events.LogicTreeAddEventEvent;
import org.terasology.scenario.internal.events.LogicTreeDeleteEvent;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HubToolScreen extends CoreScreenLayer {
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

    @In
    private AssetManager assetManager;

    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockManager;

    private Logger logger = LoggerFactory.getLogger(HubToolScreen.class);

    @Override
    public void initialise() {
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
           treeView.setContextMenuTreeProducer(node -> {
                LogicTreeMenuTreeBuilder logicTreeMenuTreeBuilder = new LogicTreeMenuTreeBuilder();
                logicTreeMenuTreeBuilder.setManager(getManager());
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_DELETE, this::delete);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_EVENT, this::addEvent);
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_ACTION, this::addAction);
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
                tempScenComponent.triggerEntities = new ArrayList<EntityRef>();
                scenarioEntity.saveComponent(tempScenComponent);
            }

            treeView.setEditor(getManager());
            treeView.setAssetManager(assetManager);
        }
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

        //Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        scenarioEntity.send(new LogicTreeAddEventEvent("new event", this));
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
        return value.isRoot();
    }

    /**
     * Action and delete follow same as the above detailed event functions.
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
        scenarioEntity.send(new LogicTreeAddActionEvent(ActionComponent.ActionType.GIVE_ITEM, this, tree.getValue().getEntity()));
    }

    private boolean checkCanAddAction() {
        Integer selectedIndex = treeView.getSelectedIndex();
        if (selectedIndex == null) {
            return false;
        }

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        LogicTreeValue value = tree.getValue();
        return value.isEvent();
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
            ExpandedComponent tempExpComponent = new ExpandedComponent();
            ScenarioComponent tempComponent = new ScenarioComponent();
            scenarioEntity = entityManager.create(tempComponent, tempExpComponent);
        }

        ScenarioComponent tempScenComponent = scenarioEntity.getComponent(ScenarioComponent.class);
        if (tempScenComponent.triggerEntities == null) {
            tempScenComponent.triggerEntities = new ArrayList<EntityRef>();
            scenarioEntity.saveComponent(tempScenComponent);
        }
    }

    /**
     * Function for updating the currently displayed tree with a new root for the scenario tree.
     *
     * @param newScenario must have a ScenarioComponent in order to build the tree detailed from it.
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
        }
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
        if (!entity.hasComponent(ExpandedComponent.class)) {
            ExpandedComponent newExp = new ExpandedComponent();
            entity.addOrSaveComponent(newExp);
        }
        ScenarioComponent scenario = entity.getComponent(ScenarioComponent.class);
        LogicTreeView tempTreeView = new LogicTreeView();
        LogicTree returnTree = new LogicTree(new LogicTreeValue("Scenario", false, assetManager.getAsset("Scenario:scenarioText", Texture.class).get(), true, entity));
        tempTreeView.setModel(returnTree.getRoot());
        if (entity.hasComponent(ExpandedComponent.class)) {
            returnTree.setExpandedNoEntity(entity.getComponent(ExpandedComponent.class).isExpanded);
        }
        if (scenario.triggerEntities != null) {
            List<EntityRef> events = scenario.triggerEntities;
            for (EntityRef e : events) {
                EventNameComponent name = e.getComponent(EventNameComponent.class);
                ActionListComponent actions = e.getComponent(ActionListComponent.class);
                LogicTreeValue e2;
                if (name == null) {
                    e2 = new LogicTreeValue(true, assetManager.getAsset("Scenario:eventText", Texture.class).get(), false, e);
                } else {
                    e2 = new LogicTreeValue(true, assetManager.getAsset("Scenario:eventText", Texture.class).get(), false, e);
                }

                LogicTree tempEventTree = new LogicTree(e2);

                if (actions != null) {
                    if (actions.actions == null) {
                        actions.actions = new ArrayList<EntityRef>();
                    }
                    for (EntityRef a : actions.actions) {
                        ActionComponent a2 = a.getComponent(ActionComponent.class);
                        if (a2 == null) {
                            tempEventTree.addChild(new LogicTreeValue(false, assetManager.getAsset("Scenario:actionText", Texture.class).get(), false, a));
                        } else {
                            tempEventTree.addChild(new LogicTreeValue(false, assetManager.getAsset("Scenario:actionText", Texture.class).get(), false, a));
                        }
                    }
                }

                returnTree.addChild(tempEventTree);
                if (e2.getEntity().hasComponent(ExpandedComponent.class)) {
                    tempEventTree.setExpandedNoEntity(e2.getEntity().getComponent(ExpandedComponent.class).isExpanded);
                }
            }
        }
        return returnTree;
    }
}
