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
import org.terasology.scenario.internal.ui.HubToolScreen;

/**
 * Event for adding an action to the logic tree. Sent to the root entity.
 * actionName: Action name, currently this is all that is needed for constructing an action.
 * hubScreen: The hub tool's screen, if this is passed then after adding the event it will update the tree on this hubScreen,
 *            if not passed(null) then it will not update immediately.
 * eventEntity: The entity of the event that the action will be attached to
 */
public class LogicTreeAddActionEvent implements Event {
    private String actionName;
    private EntityRef eventEntity;
    private HubToolScreen hubScreen;

    public LogicTreeAddActionEvent(String actionName, HubToolScreen hubScreen, EntityRef eventEntity) {
        this.actionName = actionName;
        this.hubScreen = hubScreen;
        this.eventEntity = eventEntity;
    }

    public String getActionName() {
        return actionName;
    }

    public HubToolScreen getHubScreen() {
        return hubScreen;
    }

    public EntityRef getEventEntity() {
        return eventEntity;
    }
}
