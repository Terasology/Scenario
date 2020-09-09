// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui.RegionTree;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.scenario.internal.ui.HubToolScreen;

/**
 * Simple value for displaying regions in a {@link HubToolScreen}. Just holds a entity that it matches with. Any edits
 * are done on the entity side and updates aren't made with out updated an entity which would cause a rebuild of the
 * tree anyways so no need to store temporary values within the value, searching for the components is fine.
 */
public class RegionTreeValue {
    private EntityRef entity;

    public RegionTreeValue(EntityRef entity) {
        this.entity = entity;
    }

    public EntityRef getEntity() {
        return entity;
    }

    public void setEntity(EntityRef entity) {
        this.entity = entity;
    }
}
