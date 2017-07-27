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
package org.terasology.scenario.internal.events.evaluationEvents;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;

/**
 * Event for evaluating a region entity
 */
public class EvaluateRegionEvent implements Event {
    private EntityRef result;
    private EntityRef passedEntity;

    public EvaluateRegionEvent(EntityRef passed) {
        this.passedEntity = passed;
    }

    public void setResult(EntityRef result) {
        this.result = result;
    }

    public EntityRef getResult() {
        return result;
    }

    public void setPassedEntity(EntityRef entity) {
        this.passedEntity = entity;
    }

    public EntityRef getPassedEntity() {
        return passedEntity;
    }
}
