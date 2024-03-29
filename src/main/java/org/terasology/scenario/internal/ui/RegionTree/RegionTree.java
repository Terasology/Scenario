// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui.RegionTree;

import org.terasology.nui.widgets.treeView.Tree;
import org.terasology.scenario.internal.events.RegionTreeMoveEntityEvent;
import org.terasology.scenario.internal.ui.HubToolScreen;

/**
 * Data structure to store the region entity tree on a {@link HubToolScreen}
 */
public class RegionTree extends Tree<RegionTreeValue> {
    private final HubToolScreen hubToolScreen;

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
    public Tree<RegionTreeValue> copy() {
        throw new UnsupportedOperationException();
    }
}
