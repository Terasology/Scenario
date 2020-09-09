// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

import java.util.ArrayList;
import java.util.List;

/**
 * List for events in a trigger where action entities are Scenario logic entities with an Event indicator component
 * <p>
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class TriggerEventListComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> events = new ArrayList<>();
}
