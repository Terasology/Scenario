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
import org.terasology.input.MouseInput;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.contextMenu.ContextMenuUtils;
import org.terasology.rendering.nui.contextMenu.MenuTree;
import org.terasology.rendering.nui.widgets.UITreeView;

import java.util.function.Function;

public final class LogicTreeView extends UITreeView<LogicTreeValue> {
    AssetManager assetManager;

    private Function<LogicTree, MenuTree> contextMenuTreeProducer;

    public LogicTreeView() {
        super();
        setItemRenderer(new LogicItemRenderer());
    }

    public LogicTreeView(String id){
        super(id);
        setItemRenderer(new LogicItemRenderer());
    }

    public void deleteNode(LogicTree node) {
        delete(node);
        setSelectedIndex(null);
    }

    public void addEvent(LogicTree node) {
        node.addChild(new LogicTreeValue("Sample Event", true, assetManager.getAsset("Scenario:eventText", Texture.class).get(), false));
        node.setExpanded(true);
    }

    public void addAction(LogicTree node) {
        node.addChild(new LogicTreeValue("Sample Action", false, assetManager.getAsset("Scenario:actionText", Texture.class).get(), false));
        node.setExpanded(true);
    }

    public void setContextMenuTreeProducer(Function<LogicTree, MenuTree> contextMenuTreeProducer) {
        this.contextMenuTreeProducer = contextMenuTreeProducer;
    }

    public void setEditor(NUIManager manager) {
        subscribeNodeClick((event, node) -> {
           if (event.getMouseButton() == MouseInput.MOUSE_RIGHT) {
               setSelectedIndex(getModel().indexOf(node));
               setAlternativeWidget(null);

               MenuTree menuTree = contextMenuTreeProducer.apply((LogicTree) node);
               ContextMenuUtils.showContextMenu(manager, event.getMouse().getPosition(), menuTree);
           }
        });
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
}
