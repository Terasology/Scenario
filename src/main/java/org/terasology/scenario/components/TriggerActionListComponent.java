// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;
/**
 * List for actions in a trigger where action entities are Scenario logic entities with an Action indicator component
 *
 * Scenario logic entities detailed in {@link ScenarioComponent}
 */
public class TriggerActionListComponent implements Component<TriggerActionListComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public List<EntityRef> actions = new ArrayList<>();
}
