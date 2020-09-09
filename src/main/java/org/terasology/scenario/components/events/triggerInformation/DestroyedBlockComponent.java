// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;

/**
 * Component for a block being destroyed and the information passed with it
 */
public class DestroyedBlockComponent implements Component {
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
