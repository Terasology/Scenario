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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.contextMenu.MenuTree;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LogicTreeMenuTreeBuilder {
    public static final String OPTION_ADD_ACTION = "Add Action";
    public static final String OPTION_ADD_EVENT = "Add Event";
    public static final String OPTION_DELETE = "Delete";

    private NUIManager nuiManager;

    private Logger logger = LoggerFactory.getLogger(LogicTreeMenuTreeBuilder.class);

    private Map<String, Consumer<LogicTree>> externalConsumers = Maps.newHashMap();

    private List<Consumer<LogicTree>> addContextMenuListeners = Lists.newArrayList();

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

        if (value.isRoot()) {
            primaryTree.addOption(OPTION_ADD_EVENT, externalConsumers.get(OPTION_ADD_EVENT), node);
        }

        if (value.isEvent()) {
            primaryTree.addOption(OPTION_ADD_ACTION, externalConsumers.get(OPTION_ADD_ACTION), node);
        }

        if (!node.isRoot()) {
            primaryTree.addOption(OPTION_DELETE, externalConsumers.get(OPTION_DELETE), node);
        }

        return primaryTree;
    }

}
