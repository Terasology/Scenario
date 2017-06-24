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
package org.terasology.scenario.internal.systems.ComponentEvaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.scenario.components.information.BlockComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateBlockDisplayEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateIntDisplayEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluatePlayerDisplayEvent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class EvaluationDisplaySystem extends BaseComponentSystem {

    private static Logger logger = LoggerFactory.getLogger(EvaluationDisplaySystem.class);

    @ReceiveEvent //Constant int
    public void onEvaluateIntDisplayEvent(EvaluateIntDisplayEvent event, EntityRef entity, ConstIntegerComponent comp) {
        event.setResult(Integer.toString(comp.value));
    }

    @ReceiveEvent //Block
    public void onEvaluateBlockDisplayEvent(EvaluateBlockDisplayEvent event, EntityRef entity, BlockComponent comp) {
        event.setResult(comp.value.getDisplayName());
    }

    @ReceiveEvent //Trigger/Target player
    public void onEvaluatePlayerDisplayEvent(EvaluatePlayerDisplayEvent event, EntityRef entity, PlayerComponent comp) {
        event.setResult(comp.type.name());
    }
}
