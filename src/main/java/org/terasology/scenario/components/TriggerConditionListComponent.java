// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * List for conditionals in a trigger where action entities are Scenario logic entities with an Conditional indicator component
 * <p>
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class TriggerConditionListComponent implements Component<TriggerConditionListComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> conditions = new ArrayList<>();

    @Override
    public void copyFrom(TriggerConditionListComponent other) {
        this.conditions = Lists.newArrayList(other.conditions);
    }
}
