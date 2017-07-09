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
package org.terasology.scenario.internal.ui.RegionTree;

import org.terasology.input.MouseInput;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.contextMenu.ContextMenuUtils;
import org.terasology.rendering.nui.contextMenu.MenuTree;
import org.terasology.rendering.nui.widgets.UITreeView;

import java.util.function.Function;

public class RegionTreeView extends UITreeView<RegionTreeValue> {
    private Function<RegionTree, MenuTree> contextMenuTreeProducer;

    public RegionTreeView() {
        super();
        setItemRenderer(new RegionItemRenderer());
    }

    public RegionTreeView(String id) {
        super(id);
        setItemRenderer(new RegionItemRenderer());
    }

    public void setContextMenuTreeProducer(Function<RegionTree, MenuTree> contextMenuTreeProducer) {
        this.contextMenuTreeProducer = contextMenuTreeProducer;
    }

    public void setEditor(NUIManager manager) {
        subscribeNodeClick((event, node) -> {
            if (event.getMouseButton() == MouseInput.MOUSE_RIGHT) {
                setSelectedIndex(getModel().indexOf(node));
                setAlternativeWidget(null);

                MenuTree menuTree = contextMenuTreeProducer.apply((RegionTree) node);
                ContextMenuUtils.showContextMenu(manager, event.getMouse().getPosition(), menuTree);
            }
        });
    }
}
