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
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;
import org.terasology.nui.Color;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to recolor a region entity
 *
 * It is utilizes by the {@link RegionTreeSystem}
 */
@ServerEvent
public class RegionRecolorEvent implements Event {
    private EntityRef entity;
    private Color newColor;

    public RegionRecolorEvent() {
    }

    public RegionRecolorEvent(EntityRef entity, Color newColor) {
        this.entity = entity;
        this.newColor = newColor;
    }

    public EntityRef getRegionEntity() {
        return entity;
    }

    public Color getNewColor() {
        return newColor;
    }
}
