// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.evaluationEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.scenario.components.information.ScenarioValueComparatorComponent;
import org.terasology.scenario.internal.systems.ScenarioRootManagementSystem;

/**
 * Event utilized by {@link ScenarioRootManagementSystem} in order to request a value or expression logic entity to be
 * evaluated into a comparator comparison value
 */
public class EvaluateComparatorEvent implements Event {
    private ScenarioValueComparatorComponent.comparison result;
    private EntityRef passedEntity;

    public EvaluateComparatorEvent(EntityRef passed) {
        this.passedEntity = passed;
    }

    public ScenarioValueComparatorComponent.comparison getResult() {
        return result;
    }

    public void setResult(ScenarioValueComparatorComponent.comparison result) {
        this.result = result;
    }

    public EntityRef getPassedEntity() {
        return passedEntity;
    }

    public void setPassedEntity(EntityRef entity) {
        this.passedEntity = entity;
    }
}
