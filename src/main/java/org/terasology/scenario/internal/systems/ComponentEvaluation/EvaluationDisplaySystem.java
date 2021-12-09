// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.systems.ComponentEvaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.information.ScenarioExpressionBlockCountComponent;
import org.terasology.scenario.components.information.ScenarioExpressionConcatStringComponent;
import org.terasology.scenario.components.information.ScenarioExpressionItemCountComponent;
import org.terasology.scenario.components.information.ScenarioExpressionPlayerNameComponent;
import org.terasology.scenario.components.information.ScenarioExpressionRandomIntComponent;
import org.terasology.scenario.components.information.ScenarioExpressionRegionNameComponent;
import org.terasology.scenario.components.information.ScenarioValueBlockFamilyComponent;
import org.terasology.scenario.components.information.ScenarioValueBlockUriComponent;
import org.terasology.scenario.components.information.ScenarioValueComparatorComponent;
import org.terasology.scenario.components.information.ScenarioValueIntegerComponent;
import org.terasology.scenario.components.information.ScenarioValueItemPrefabUriComponent;
import org.terasology.scenario.components.information.ScenarioValuePlayerComponent;
import org.terasology.scenario.components.information.ScenarioValueRegionComponent;
import org.terasology.scenario.components.information.ScenarioValueStringComponent;
import org.terasology.scenario.components.information.ScenarioValueTriggeringBlockComponent;
import org.terasology.scenario.components.information.ScenarioValueTriggeringRegionComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateDisplayEvent;

import java.util.Map;

/**
 * This is a system that takes argument entities that contain value or expression components and evaluates them into a string that is used
 * for display.
 * <p>
 * Events watched are {@link EvaluateDisplayEvent}
 * <p>
 * Argument entities include: Network Component Type Component Value or Expression Component (Values are constant values, expressions are
 * evaluated to obtain the value)
 */
@RegisterSystem(RegisterMode.CLIENT)
public class EvaluationDisplaySystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationDisplaySystem.class);

    @In
    BlockManager blockManager;

    @In
    PrefabManager prefabManager;

    @ReceiveEvent //Constant int
    public void onEvaluateIntDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueIntegerComponent comp) {
        event.setResult(Integer.toString(comp.value));
    }

    @ReceiveEvent //Block
    public void onEvaluateBlockDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueBlockFamilyComponent comp) {
        event.setResult(comp.value.getDisplayName());
    }

    @ReceiveEvent //Trigger Block
    public void onEvaluateDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueTriggeringBlockComponent comp) {
        event.setResult("Triggering Block");
    }

    @ReceiveEvent //BlockConstant
    public void onEvaluateBlockDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueBlockUriComponent comp) {
        event.setResult(blockManager.getBlockFamily(comp.blockUri).getDisplayName());
    }

    @ReceiveEvent //Trigger/Target player
    public void onEvaluatePlayerDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValuePlayerComponent comp) {
        event.setResult(comp.type.name());
    }

    @ReceiveEvent //Constant string
    public void onEvaluateStringDisplayEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueStringComponent comp) {
        event.setResult(comp.string);
    }

    @ReceiveEvent //Comparator
    public void onEvaluateStringEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueComparatorComponent comp) {
        event.setResult(comp.compare.toString());
    }

    @ReceiveEvent
    public void onEvaluateRandomIntEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionRandomIntComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalInt1 = new EvaluateDisplayEvent();
        args.get("int1").send(evalInt1);
        String int1 = evalInt1.getResult();

        EvaluateDisplayEvent evalInt2 = new EvaluateDisplayEvent();
        args.get("int2").send(evalInt2);
        String int2 = evalInt2.getResult();

        event.setResult("Random(" + int1 + " - " + int2 + ")");
    }

    @ReceiveEvent
    public void onEvaluateItemEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueItemPrefabUriComponent comp) {
        event.setResult(comp.prefabURI);
    }

    @ReceiveEvent //Count of items
    public void onEvaluateIntEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionItemCountComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalItem = new EvaluateDisplayEvent();
        args.get("item").send(evalItem);
        String itemName = evalItem.getResult();

        EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
        args.get("player").send(evalPlayer);
        String player = evalPlayer.getResult();

        event.setResult("Count of " + itemName + " owned by " + player);
    }

    @ReceiveEvent //Count of blocks
    public void onEvaluateIntEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalBlock = new EvaluateDisplayEvent();
        args.get("block").send(evalBlock);
        String itemName = evalBlock.getResult();

        EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
        args.get("player").send(evalPlayer);
        String player = evalPlayer.getResult();

        event.setResult("Count of " + itemName + " owned by " + player);
    }

    @ReceiveEvent //PlayerName
    public void onEvaluatePlayerNameEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionPlayerNameComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
        args.get("player").send(evalPlayer);
        String player = evalPlayer.getResult();

        event.setResult("Name of " + player);
    }

    @ReceiveEvent //RegionName
    public void onEvaluateRegionEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioValueRegionComponent comp) {
        if (comp.regionEntity != null) {
            event.setResult(comp.regionEntity.getComponent(RegionNameComponent.class).regionName);
        } else {
            event.setResult("No region selected");
        }
    }

    @ReceiveEvent //ConcatString
    public void onEvaluateConcatStringEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionConcatStringComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalStr1 = new EvaluateDisplayEvent();
        args.get("string1").send(evalStr1);
        String str1 = evalStr1.getResult();

        EvaluateDisplayEvent evalStr2 = new EvaluateDisplayEvent();
        args.get("string2").send(evalStr2);
        String str2 = evalStr2.getResult();

        event.setResult(str1 + str2);
    }

    @ReceiveEvent //Triggering Region
    public void onEvaluateDisplayTriggeringRegionEvent(EvaluateDisplayEvent event, EntityRef entity,
                                                       ScenarioValueTriggeringRegionComponent comp) {
        event.setResult("TRIGGERING REGION");
    }

    @ReceiveEvent //Name of Region
    public void onEvaluateNameOfRegion(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionRegionNameComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalName = new EvaluateDisplayEvent();
        args.get("region").send(evalName);
        String region = evalName.getResult();

        event.setResult("Name of " + region);
    }
}
