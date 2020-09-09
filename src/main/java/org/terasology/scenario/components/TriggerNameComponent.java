// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

/**
 * Component that includes the details about a trigger, currently it is a name and three empty entities that are used
 * for the entity expansion of a hub tool that represent the blank event/condition/action portions of the UI
 */
public class TriggerNameComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String name;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef entityForEvent;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef entityForCondition;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef entityForAction;
}
