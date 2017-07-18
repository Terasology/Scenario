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
package org.terasology.scenario.internal.ui.LogicTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.rendering.nui.widgets.treeView.Tree;
import org.terasology.scenario.components.ExpandedComponent;
import org.terasology.scenario.components.TriggerNameComponent;
import org.terasology.scenario.internal.events.LogicTreeMoveEntityEvent;
import org.terasology.scenario.internal.ui.HubToolScreen;


public class LogicTree extends Tree<LogicTreeValue> {

    private HubToolScreen hubToolScreen;

    private static final Logger logger = LoggerFactory.getLogger(LogicTree.class);
    public LogicTree(HubToolScreen hubToolScreen) {
        this.hubToolScreen = hubToolScreen;
    }

    public LogicTree(LogicTreeValue value, HubToolScreen hubToolScreen) {
        setValue(value);
        this.hubToolScreen = hubToolScreen;
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            if (value.getEntity() != null) {
                switch (value.getValueType()) {
                    //If it is a name(event/conditional/action) then it needs to find the matching entity from the trigger
                    case EVENT_NAME:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.add(this.getValue().getEntity().getComponent(TriggerNameComponent.class).entityForEvent);
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                    case CONDITIONAL_NAME:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.add(this.getValue().getEntity().getComponent(TriggerNameComponent.class).entityForCondition);
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                    case ACTION_NAME:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.add(this.getValue().getEntity().getComponent(TriggerNameComponent.class).entityForAction);
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                    default:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.add(this.getValue().getEntity());
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                }

            }
        }
        else {
            if (value.getEntity() != null) {
                switch (value.getValueType()) {
                    //If it is a name(event/conditional/action) then it needs to find the matching entity from the trigger
                    case EVENT_NAME:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.remove(this.getValue().getEntity().getComponent(TriggerNameComponent.class).entityForEvent);
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                    case CONDITIONAL_NAME:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.remove(this.getValue().getEntity().getComponent(TriggerNameComponent.class).entityForCondition);
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                    case ACTION_NAME:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.remove(this.getValue().getEntity().getComponent(TriggerNameComponent.class).entityForAction);
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                    default:
                        hubToolScreen.getEntity().getComponent(ExpandedComponent.class).expandedList.remove(this.getValue().getEntity());
                        hubToolScreen.getEntity().saveComponent(hubToolScreen.getEntity().getComponent(ExpandedComponent.class));
                        break;
                }
            }
        }
    }

    @Override
    public boolean acceptsChild(Tree<LogicTreeValue> child) {
        boolean thisReturn = false;
        switch (this.getValue().getValueType()) {
            case EVENT_NAME:
                if (child.getValue().getValueType() == LogicTreeValue.Type.EVENT) {
                    thisReturn = true;
                }
                break;
            case CONDITIONAL_NAME:
                if (child.getValue().getValueType() == LogicTreeValue.Type.CONDITIONAL) {
                    thisReturn = true;
                }
                break;
            case ACTION_NAME:
                if (child.getValue().getValueType() == LogicTreeValue.Type.ACTION) {
                    thisReturn = true;
                }
                break;
            case TRIGGER:
                if (child.getValue().getValueType() == LogicTreeValue.Type.EVENT_NAME || //Can't move event/cond/action names
                        child.getValue().getValueType() == LogicTreeValue.Type.CONDITIONAL_NAME ||
                        child.getValue().getValueType() == LogicTreeValue.Type.ACTION_NAME) {
                    thisReturn = true;
                }
            case SCENARIO:
                if (child.getValue().getValueType() == LogicTreeValue.Type.TRIGGER) {
                    thisReturn = true;
                }
                break;
        }

        return thisReturn && super.acceptsChild(child);
    }

    public void setExpandedNoEntity(boolean expanded) {
        super.setExpanded(expanded);
    }

    @Override
    public void addChild(LogicTreeValue childValue){
        addChild(new LogicTree(childValue, hubToolScreen));
    }

    @Override
    public void addChild(Tree<LogicTreeValue> child) {
        super.addChild(child);
    }

    @Override
    public void addChild(int index, Tree<LogicTreeValue> child) { //Should only send if moving entities on UI view
        hubToolScreen.getScenarioEntity().send(new LogicTreeMoveEntityEvent(this.getValue().getEntity(), child.getValue().getEntity(), child.getValue().getValueType(), index));
    }

    @Override
    public Tree<LogicTreeValue> copy(){
        throw new UnsupportedOperationException();
    }
}
