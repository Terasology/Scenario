// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.NoReplicate;
import org.terasology.engine.network.Replicate;

/**
 * Component that lets the client know that the scenario entity was updated and therefore it should redraw on the next
 * update
 */
public class ScenarioHubToolUpdateComponent implements Component {
    @NoReplicate
    public String localScreenID;

    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public EntityRef addedEntity;
}
