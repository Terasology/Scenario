// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.EntityTreeSystem;

/**
 * Event for adding a trigger to the logic tree. Sent to the root entity. hubScreen: The hub tool's screen, if this is passed then after
 * adding the event it will update the tree on this hubScreen, if not passed(null) then it will not update immediately.
 * <p>
 * Utilized with {@link EntityTreeSystem}
 */
@ServerEvent
public class LogicTreeAddTriggerEvent implements Event {
    private EntityRef hubScreen;

    public LogicTreeAddTriggerEvent() {
    }

    public LogicTreeAddTriggerEvent(EntityRef hubScreen) {
        this.hubScreen = hubScreen;
    }

    public EntityRef getHubScreen() {
        return hubScreen;
    }
}
