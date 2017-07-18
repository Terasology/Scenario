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
package org.terasology.scenario.internal.events;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.network.ServerEvent;
import org.terasology.scenario.internal.ui.HubToolScreen;

@ServerEvent
public class RegionRenameEvent implements Event {
    private EntityRef entity;
    private String newName;

    public RegionRenameEvent() {
    }

    public RegionRenameEvent(EntityRef entity, String newName) {
        this.entity = entity;
        this.newName = newName;
    }

    public EntityRef getRegionEntity() {
        return entity;
    }

    public String getNewName() {
        return newName;
    }
}
