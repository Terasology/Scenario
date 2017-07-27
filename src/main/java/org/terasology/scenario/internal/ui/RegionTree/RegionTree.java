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

import org.terasology.rendering.nui.widgets.treeView.Tree;
import org.terasology.scenario.internal.events.RegionTreeMoveEntityEvent;
import org.terasology.scenario.internal.ui.HubToolScreen;

/**
 * Data structure to store the region entity tree on a hubtool
 */
public class RegionTree extends Tree<RegionTreeValue> {
    private HubToolScreen hubToolScreen;

    public RegionTree(HubToolScreen hubToolScreen) {
        this.hubToolScreen = hubToolScreen;
    }

    public RegionTree(RegionTreeValue value, HubToolScreen hubToolScreen) {
        setValue(value);
        this.hubToolScreen = hubToolScreen;
    }

    @Override
    public boolean acceptsChild(Tree<RegionTreeValue> child) {
        return (getValue().getEntity() == null && child.getValue().getEntity() != null) && super.acceptsChild(child);
    }

    @Override
    public void addChild(RegionTreeValue childValue) {
        addChild(new RegionTree(childValue, hubToolScreen));
    }

    @Override
    public void addChild(int index, Tree<RegionTreeValue> child) { //Allows for re-ordering of regions to actually maintain
        hubToolScreen.getScenarioEntity().send(new RegionTreeMoveEntityEvent(child.getValue().getEntity(), index));
    }

    @Override
    public Tree<RegionTreeValue> copy(){
        throw new UnsupportedOperationException();
    }
}
