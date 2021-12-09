// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui.LogicTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.contextMenu.MenuTree;
import org.terasology.scenario.internal.ui.HubToolScreen;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder that constructs the context menu for a logic entity on the logic tree view of a {@link HubToolScreen}.
 */
public class LogicTreeMenuTreeBuilder {
    public static final String OPTION_ADD_ACTION = "Add Action";
    public static final String OPTION_ADD_EVENT = "Add Event";
    public static final String OPTION_DELETE = "Delete";
    public static final String OPTION_ADD_CONDITIONAL = "Add Conditional";
    public static final String OPTION_ADD_TRIGGER = "Add Trigger";
    public static final String OPTION_EDIT = "Edit";


    private NUIManager nuiManager;

    private final Logger logger = LoggerFactory.getLogger(LogicTreeMenuTreeBuilder.class);

    private final Map<String, Consumer<LogicTree>> externalConsumers = Maps.newHashMap();

    private final List<Consumer<LogicTree>> addContextMenuListeners = Lists.newArrayList();

    public void setManager(NUIManager manager) {
        this.nuiManager = manager;
    }

    public void putConsumer(String key, Consumer<LogicTree> value) {
        externalConsumers.put(key, value);
    }

    public void subscribeAddContextMenu(Consumer<LogicTree> listener) {
        addContextMenuListeners.add(listener);
    }

    public MenuTree createPrimaryContextMenu(LogicTree node) {
        MenuTree primaryTree = new MenuTree(null);

        LogicTreeValue value = node.getValue();

        switch (value.getValueType()) {
            case SCENARIO:
                primaryTree.addOption(OPTION_ADD_TRIGGER, externalConsumers.get(OPTION_ADD_TRIGGER), node);
                break;
            case TRIGGER:
                primaryTree.addOption(OPTION_ADD_EVENT, externalConsumers.get(OPTION_ADD_EVENT), node);
                primaryTree.addOption(OPTION_ADD_CONDITIONAL, externalConsumers.get(OPTION_ADD_CONDITIONAL), node);
                primaryTree.addOption(OPTION_ADD_ACTION, externalConsumers.get(OPTION_ADD_ACTION), node);
                primaryTree.addOption(OPTION_DELETE, externalConsumers.get(OPTION_DELETE), node);
                break;
            case EVENT_NAME:
                primaryTree.addOption(OPTION_ADD_EVENT, externalConsumers.get(OPTION_ADD_EVENT), node);
                break;
            case CONDITIONAL_NAME:
                primaryTree.addOption(OPTION_ADD_CONDITIONAL, externalConsumers.get(OPTION_ADD_CONDITIONAL), node);
                break;
            case ACTION_NAME:
                primaryTree.addOption(OPTION_ADD_ACTION, externalConsumers.get(OPTION_ADD_ACTION), node);
                break;
            case ACTION:
            case CONDITIONAL:
            case EVENT:
                primaryTree.addOption(OPTION_DELETE, externalConsumers.get(OPTION_DELETE), node);
                primaryTree.addOption(OPTION_EDIT, externalConsumers.get(OPTION_EDIT), node);
                break;
        }

        return primaryTree;
    }


}
