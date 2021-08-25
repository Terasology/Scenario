// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.NoReplicate;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component that lets the client know that the scenario entity was updated and therefore it should redraw on the next update
 */
public class ScenarioHubToolUpdateComponent implements Component<ScenarioHubToolUpdateComponent> {
    @NoReplicate
    public String localScreenID;

    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public EntityRef addedEntity;

    @Override
    public void copyFrom(ScenarioHubToolUpdateComponent other) {
        this.localScreenID = other.localScreenID;
        this.addedEntity = other.addedEntity;
    }
}
