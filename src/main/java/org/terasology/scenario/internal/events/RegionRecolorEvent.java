// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.nui.Color;
import org.terasology.scenario.internal.systems.RegionTreeSystem;

/**
 * Event that is called to recolor a region entity
 * <p>
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
