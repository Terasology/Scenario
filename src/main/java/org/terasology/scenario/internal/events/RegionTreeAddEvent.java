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

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to begin a region creation, only sets up a region entity that is in process of being created, does not completely
 * add to the region entity tree yet
 *
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionTreeAddEvent implements Event {
    private EntityRef creatingEntity;

    public RegionTreeAddEvent() {}

    public RegionTreeAddEvent(EntityRef creatingEntity) {
        this.creatingEntity = creatingEntity;
    }

    public EntityRef getCreatingEntity() {
        return creatingEntity;
    }
}
