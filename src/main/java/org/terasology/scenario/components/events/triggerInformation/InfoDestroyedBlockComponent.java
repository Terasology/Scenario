// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 */
public class InfoDestroyedBlockComponent implements Component<InfoDestroyedBlockComponent> {
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

    @Override
    public void copy(InfoDestroyedBlockComponent other) {
        this.destroyedBlock = other.destroyedBlock;
        this.directCause = other.directCause;
        this.damageType = other.damageType;
    }
}
