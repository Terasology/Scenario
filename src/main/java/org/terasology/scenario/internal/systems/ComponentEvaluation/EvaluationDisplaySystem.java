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
import org.terasology.registry.In;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.information.BlockComponent;
import org.terasology.scenario.components.information.ConstBlockComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.ConstStringComponent;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.components.information.RandomIntComponent;
import org.terasology.scenario.components.information.TriggeringBlockComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateDisplayEvent;
import org.terasology.world.block.BlockManager;

import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class EvaluationDisplaySystem extends BaseComponentSystem {

    private static Logger logger = LoggerFactory.getLogger(EvaluationDisplaySystem.class);

    @In
    BlockManager blockManager;

    @ReceiveEvent //Constant int
    public void onEvaluateIntDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ConstIntegerComponent comp) {
        event.setResult(Integer.toString(comp.value));
    }

    @ReceiveEvent //Block
    public void onEvaluateBlockDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, BlockComponent comp) {
        event.setResult(comp.value.getDisplayName());
    }

    @ReceiveEvent //Trigger Block
    public void onEvaluateDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, TriggeringBlockComponent comp) {
        event.setResult("Triggering Block");
    }

    @ReceiveEvent //BlockConstant
    public void onEvaluateBlockDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ConstBlockComponent comp) {
        event.setResult(blockManager.getBlockFamily(comp.block_uri).getDisplayName());
    }

    @ReceiveEvent //Trigger/Target player
    public void onEvaluatePlayerDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, PlayerComponent comp) {
        event.setResult(comp.type.name());
    }

    @ReceiveEvent //Constant string
    public void onEvaluateStringDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ConstStringComponent comp) {
        event.setResult(comp.string);
    }

    @ReceiveEvent
    public void OnEvaluateRandomIntEvent(EvaluateDisplayEvent event, EntityRef entity, RandomIntComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalInt1 = new EvaluateDisplayEvent();
        args.get("int1").send(evalInt1);
        String int1 = evalInt1.getResult();

        EvaluateDisplayEvent evalInt2 = new EvaluateDisplayEvent();
        args.get("int2").send(evalInt2);
        String int2 = evalInt2.getResult();

        event.setResult("Random(" + int1 + " - " + int2 + ")");
    }
}
