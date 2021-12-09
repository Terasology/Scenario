// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui.RegionTree;

import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.contextMenu.ContextMenuUtils;
import org.terasology.engine.rendering.nui.contextMenu.MenuTree;
import org.terasology.input.MouseInput;
import org.terasology.nui.widgets.UITreeView;
import org.terasology.scenario.internal.ui.HubToolScreen;

import java.util.function.Function;

/**
 * Setting up the display and context menu of the region entity tree on a {@link HubToolScreen}
 */
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
