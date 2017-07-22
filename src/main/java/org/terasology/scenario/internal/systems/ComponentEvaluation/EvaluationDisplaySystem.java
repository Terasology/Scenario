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
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.information.BlockComponent;
import org.terasology.scenario.components.information.ConcatStringComponent;
import org.terasology.scenario.components.information.ConstBlockComponent;
import org.terasology.scenario.components.information.ConstComparatorComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.ConstItemPrefabComponent;
import org.terasology.scenario.components.information.ConstRegionComponent;
import org.terasology.scenario.components.information.ConstStringComponent;
import org.terasology.scenario.components.information.ItemCountComponent;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.components.information.PlayerNameComponent;
import org.terasology.scenario.components.information.RandomIntComponent;
import org.terasology.scenario.components.information.RegionNameStringComponent;
import org.terasology.scenario.components.information.TriggeringBlockComponent;
import org.terasology.scenario.components.information.TriggeringRegionComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateDisplayEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateStringEvent;
import org.terasology.world.block.BlockManager;

import java.util.Map;

@RegisterSystem(RegisterMode.CLIENT)
public class EvaluationDisplaySystem extends BaseComponentSystem {

    private static Logger logger = LoggerFactory.getLogger(EvaluationDisplaySystem.class);

    @In
    BlockManager blockManager;

    @In
    PrefabManager prefabManager;

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

    @ReceiveEvent //Comparator
    public void onEvaluateStringEvent(EvaluateDisplayEvent event, EntityRef entity, ConstComparatorComponent comp) {
        event.setResult(comp.compare.toString());
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

    @ReceiveEvent
    public void OnEvaluateItemEvent(EvaluateDisplayEvent event, EntityRef entity, ConstItemPrefabComponent comp) {
        event.setResult(comp.prefabURI);
    }

    @ReceiveEvent //Count of items
    public void onEvaluateIntEvent(EvaluateDisplayEvent event, EntityRef entity, ItemCountComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalItem = new EvaluateDisplayEvent();
        args.get("item").send(evalItem);
        String itemName = evalItem.getResult();

        EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
        args.get("player").send(evalPlayer);
        String player = evalPlayer.getResult();

        event.setResult("Count of " + itemName + " owned by " + player);
    }

    @ReceiveEvent //PlayerName
    public void OnEvaluatePlayerNameEvent(EvaluateDisplayEvent event, EntityRef entity, PlayerNameComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
        args.get("player").send(evalPlayer);
        String player = evalPlayer.getResult();

        event.setResult("Name of " + player);
    }

    @ReceiveEvent //RegionName
    public void OnEvaluateRegionEvent(EvaluateDisplayEvent event, EntityRef entity, ConstRegionComponent comp) {
        if (comp.regionEntity != null) {
            event.setResult(comp.regionEntity.getComponent(RegionNameComponent.class).regionName);
        }
        else {
            event.setResult("No region selected");
        }
    }

    @ReceiveEvent //ConcatString
    public void onEvaluateConcatStringEvent(EvaluateDisplayEvent event, EntityRef entity, ConcatStringComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalStr1 = new EvaluateDisplayEvent();
        args.get("string1").send(evalStr1);
        String str1 = evalStr1.getResult();

        EvaluateDisplayEvent evalStr2 = new EvaluateDisplayEvent();
        args.get("string2").send(evalStr2);
        String str2 = evalStr2.getResult();

        event.setResult(str1 + str2);
    }

    @ReceiveEvent //Triggering Region
    public void onEvaluateDisplayTriggeringRegionEvent(EvaluateDisplayEvent event, EntityRef entity, TriggeringRegionComponent comp) {
        event.setResult("TRIGGERING REGION");
    }

    @ReceiveEvent //Name of Region
    public void OnEvaluateNameOfRegion(EvaluateDisplayEvent event, EntityRef entity, RegionNameStringComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalName = new EvaluateDisplayEvent();
        args.get("region").send(evalName);
        String region = evalName.getResult();

        event.setResult("Name of " + region);
    }
}
