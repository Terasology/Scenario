// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;

/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoDestroyedBlockComponent implements Component {
    /**
     * entity of the block that was destroyed
     */
    public EntityRef destroyedBlock;

    /**
     * entity of the character that destroyed the block
     */
    public EntityRef directCause;

    /**
     * prefab of the item used to destroy the block
     */
    public Prefab damageType;
}
