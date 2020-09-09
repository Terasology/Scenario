// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.regions;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.ScenarioComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Creation component for a Scenario argument entity, includes a list of player character entities that are contained
 * within the region
 * <p>
 * Scenario region entities are detailed in {@link ScenarioComponent}
 */
public class RegionContainingEntitiesComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> entities = new ArrayList<>();
}
