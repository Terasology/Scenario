// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

import java.util.HashSet;
import java.util.Set;

/**
 * Component that gets attached to a scenario hub tool that contains the list of entities that are expanded in the
 * treeview of the logic entities
 */
public class HubToolExpansionComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Set<EntityRef> expandedList = new HashSet<>();
}
