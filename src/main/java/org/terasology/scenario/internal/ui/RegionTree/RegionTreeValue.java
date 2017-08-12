/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.scenario.internal.ui.RegionTree;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.scenario.internal.ui.HubToolScreen;

/**
 * Simple value for displaying regions in a {@link HubToolScreen}. Just holds a entity that it matches with.
 * Any edits are done on the entity side and updates aren't made with out updated an entity which would cause a rebuild
 * of the tree anyways so no need to store temporary values within the value, searching for the components is fine.
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
