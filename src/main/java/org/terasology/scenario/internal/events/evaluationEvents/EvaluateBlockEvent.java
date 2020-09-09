// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events.evaluationEvents;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.scenario.internal.systems.ScenarioRootManagementSystem;

/**
 * Event utilized by {@link ScenarioRootManagementSystem} in order to request a value or expression logic entity to be
 * evaluated into a BlockFamily
 */
public class EvaluateBlockEvent implements Event {

    private BlockFamily result;
    private EntityRef passedEntity;

    public EvaluateBlockEvent(EntityRef passed) {
        this.passedEntity = passed;
    }

    public BlockFamily getResult() {
        return result;
    }

    public void setResult(BlockFamily result) {
        this.result = result;
    }

    public EntityRef getPassedEntity() {
        return passedEntity;
    }

    public void setPassedEntity(EntityRef entity) {
        this.passedEntity = entity;
    }
}
