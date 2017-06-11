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
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.rendering.nui.widgets.treeView.Tree;

import java.util.HashSet;
import java.util.Set;


public class LogicTree extends Tree<LogicTreeValue> {

    public Set<EntityRef> expandedList;

    private static final Logger logger = LoggerFactory.getLogger(LogicTree.class);
    public LogicTree() {

    }

    public LogicTree(LogicTreeValue value) {
        setValue(value);
        expandedList = new HashSet<EntityRef>();
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        //ExpandedComponent exp = getValue().getEntity().getComponent(ExpandedComponent.class);
        if (expanded) {
            expandedList.add(getValue().getEntity());
        }
        else {
            expandedList.remove(getValue().getEntity());
        }
        /*if (exp != null) {
            exp.isExpanded = expanded;

        }*/
    }

    public void setExpandedNoEntity(boolean expanded) {
        super.setExpanded(expanded);
    }

    @Override
    public void addChild(LogicTreeValue childValue){
        addChild(new LogicTree(childValue));
    }

    @Override
    public Tree<LogicTreeValue> copy(){
        throw new UnsupportedOperationException();
    }
}
