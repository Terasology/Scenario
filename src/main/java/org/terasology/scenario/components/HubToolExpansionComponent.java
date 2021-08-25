// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import com.google.common.collect.Sets;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Component that gets attached to a scenario hub tool that contains the list of entities that are expanded in the treeview of the logic entities
 */
public class HubToolExpansionComponent implements Component<HubToolExpansionComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Set<EntityRef> expandedList = new HashSet<>();

    @Override
    public void copyFrom(HubToolExpansionComponent other) {
        this.expandedList = Sets.newHashSet(other.expandedList);
    }
}
