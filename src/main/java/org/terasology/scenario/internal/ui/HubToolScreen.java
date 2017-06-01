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

import org.terasology.assets.management.AssetManager;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UIBox;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.treeView.Tree;

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

    private LogicTreeView treeView;

    @In
    private AssetManager assetManager;

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
                logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_DELETE, getEditor()::deleteNode);
               logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_EVENT, getEditor()::addEvent);
               logicTreeMenuTreeBuilder.putConsumer(LogicTreeMenuTreeBuilder.OPTION_ADD_ACTION, getEditor()::addAction);
                logicTreeMenuTreeBuilder.subscribeAddContextMenu(n -> {
                    getEditor().fireUpdateListeners();
                });

                return logicTreeMenuTreeBuilder.createPrimaryContextMenu(node);
            });

            LogicTree tree = new LogicTree(new LogicTreeValue("Scenario", false, assetManager.getAsset("Scenario:scenarioText", Texture.class).get(), true));
            //LogicTreeValue temp = new LogicTreeValue("Sample Event", true, assetManager.getAsset("Scenario:eventText", Texture.class).get(), false);
            //tree.addChild(temp);
            treeView.setModel(tree);
            //Tree<LogicTreeValue> tempTree = treeView.getModel().getNodeByValue(temp);
            //LogicTreeValue temp2 = new LogicTreeValue("Sample Action", false, assetManager.getAsset("Scenario:actionText", Texture.class).get(), false);
            //tempTree.addChild(temp2);

            treeView.setEditor(getManager());
            treeView.setAssetManager(assetManager);
        }
    }

    private void onAddEventButton(UIWidget button) {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        tree.setExpanded(true);
        tree.addChild(new LogicTreeValue("Sample Event", true, assetManager.getAsset("Scenario:eventText", Texture.class).get(), false));
    }

    private boolean checkCanAddEvent() {
        Integer selectedIndex = treeView.getSelectedIndex();
        if (selectedIndex == null) {
            return false;
        }

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        LogicTreeValue value = tree.getValue();
        return value.isRoot();
    }

    private void onAddActionButton(UIWidget button) {
        Integer selectedIndex = treeView.getSelectedIndex();

        Tree<LogicTreeValue> tree = treeView.getModel().getNode(selectedIndex);
        tree.setExpanded(true);
        tree.addChild(new LogicTreeValue("Sample Action", false, assetManager.getAsset("Scenario:actionText", Texture.class).get(), false));
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
        Integer selectedIndex = treeView.getSelectedIndex();

        treeView.getModel().removeNode(selectedIndex);
        treeView.setSelectedIndex(0);
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
}
