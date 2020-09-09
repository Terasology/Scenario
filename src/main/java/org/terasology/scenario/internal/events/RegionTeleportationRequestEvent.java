// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class RegionTeleportationRequestEvent implements Event {
    private EntityRef teleportedEntity;
    private EntityRef requestedRegion;

    public RegionTeleportationRequestEvent() {
    }

    public RegionTeleportationRequestEvent(EntityRef teleportedEntity, EntityRef requestedRegion) {
        this.teleportedEntity = teleportedEntity;
        this.requestedRegion = requestedRegion;
    }

    public EntityRef getTeleportedEntity() {
        return teleportedEntity;
    }

    public EntityRef getRequestedRegion() {
        return requestedRegion;
    }
}
