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
 * Component attached to a hubtool that includes a set of all of the region entities that should be visible to the local player (visibility
 * of the regions are ticked to true)
 */
@Replicate(FieldReplicateType.SERVER_TO_OWNER)
public class ScenarioRegionVisibilityComponent implements Component<ScenarioRegionVisibilityComponent> {
    @Replicate(FieldReplicateType.OWNER_TO_SERVER)
    public Set<EntityRef> visibleList = new HashSet<>();

    @Override
    public void copyFrom(ScenarioRegionVisibilityComponent other) {
        visibleList = Sets.newHashSet(other.visibleList);
    }
}
