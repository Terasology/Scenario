// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.EntityTreeSystem;

/**
 * Event for adding an action to the logic tree. Sent to the root entity. hubScreen: The hub tool's screen, if this is passed then after
 * adding the event it will update the tree on this hubScreen, if not passed(null) then it will not update immediately. triggerEntity: The
 * entity of the trigger that the action will be attached to
 * <p>
 * Utilized with {@link EntityTreeSystem}
 */
@ServerEvent
public class LogicTreeAddActionEvent implements Event {
    private EntityRef triggerEntity;
    private EntityRef hubScreen;

    public LogicTreeAddActionEvent() {
    }

    public LogicTreeAddActionEvent(EntityRef hubScreen, EntityRef triggerEntity) {
        this.hubScreen = hubScreen;
        this.triggerEntity = triggerEntity;
    }

    public EntityRef getHubScreen() {
        return hubScreen;
    }

    public EntityRef getTriggerEntity() {
        return triggerEntity;
    }
}
