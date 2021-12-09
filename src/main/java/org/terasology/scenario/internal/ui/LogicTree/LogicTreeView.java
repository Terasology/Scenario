// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui.LogicTree;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.contextMenu.ContextMenuUtils;
import org.terasology.engine.rendering.nui.contextMenu.MenuTree;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.input.MouseInput;
import org.terasology.nui.widgets.UITreeView;

import java.util.Set;
import java.util.function.Function;

/**
 * Class the details the actual displaying portion of the {@link LogicTree}
 */
public final class LogicTreeView extends UITreeView<LogicTreeValue> {
    public Set<EntityRef> expandedList;
    private AssetManager assetManager;
    private Function<LogicTree, MenuTree> contextMenuTreeProducer;

    public LogicTreeView() {
        super();
        setItemRenderer(new LogicItemRenderer());
    }

    public LogicTreeView(String id) {
        super(id);
        setItemRenderer(new LogicItemRenderer());
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
