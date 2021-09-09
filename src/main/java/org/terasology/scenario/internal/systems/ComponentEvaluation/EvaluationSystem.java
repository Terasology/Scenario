// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.systems.ComponentEvaluation;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.nui.FontColor;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.conditionals.ScenarioSecondaryBlockCompareComponent;
import org.terasology.scenario.components.conditionals.ScenarioSecondaryIntCompareComponent;
import org.terasology.scenario.components.conditionals.ScenarioSecondaryPlayerRegionComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoDestroyedBlockComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoTriggerRegionComponent;
import org.terasology.scenario.components.events.triggerInformation.InfoTriggeringEntityComponent;
import org.terasology.scenario.components.information.ScenarioExpressionBlockCountComponent;
import org.terasology.scenario.components.information.ScenarioExpressionConcatStringComponent;
import org.terasology.scenario.components.information.ScenarioExpressionItemCountComponent;
import org.terasology.scenario.components.information.ScenarioExpressionPlayerNameComponent;
import org.terasology.scenario.components.information.ScenarioExpressionRandomIntComponent;
import org.terasology.scenario.components.information.ScenarioExpressionRegionNameComponent;
import org.terasology.scenario.components.information.ScenarioValueBlockUriComponent;
import org.terasology.scenario.components.information.ScenarioValueComparatorComponent;
import org.terasology.scenario.components.information.ScenarioValueIntegerComponent;
import org.terasology.scenario.components.information.ScenarioValueItemPrefabUriComponent;
import org.terasology.scenario.components.information.ScenarioValuePlayerComponent;
import org.terasology.scenario.components.information.ScenarioValueRegionComponent;
import org.terasology.scenario.components.information.ScenarioValueStringComponent;
import org.terasology.scenario.components.information.ScenarioValueTriggeringBlockComponent;
import org.terasology.scenario.components.information.ScenarioValueTriggeringRegionComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.evaluationEvents.ConditionalCheckEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateBlockEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateComparatorEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateIntEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateItemPrefabEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateRegionEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateStringEvent;

import java.util.Map;

/**
 * This is a system that takes argument entities that contain value or expression components and evaluates them
 * into the actual value type that can be used
 *
 * Events watched are any evaluation events that are not the display evaluation
 *
 * Argument entities include:
 *   Network Component
 *   Type Component
 *   Value or Expression Component (Values are constant values, expressions are evaluated to obtain the value)
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class EvaluationSystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(EvaluationSystem.class);

    @In
    BlockManager blockManager;

    @In
    PrefabManager prefabManager;

    @ReceiveEvent
    public void onEvaluateIntEvent(EvaluateIntEvent event, EntityRef entity, ScenarioValueIntegerComponent component) {
        event.setResult(component.value);
    }

    @ReceiveEvent
    public void onEvaluateStringEvent(EvaluateStringEvent event, EntityRef entity, ScenarioValueStringComponent component) {
        event.setResult(component.string);
    }

    @ReceiveEvent
    public void onEvaluateIntEvent(EvaluateIntEvent event, EntityRef entity, ScenarioExpressionRandomIntComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateIntEvent evalInt1 = new EvaluateIntEvent(event.getPassedEntity());
        args.get("int1").send(evalInt1);
        int int1 = evalInt1.getResult();

        EvaluateIntEvent evalInt2 = new EvaluateIntEvent(event.getPassedEntity());
        args.get("int2").send(evalInt2);
        int int2 = evalInt2.getResult();

        Random rand = new FastRandom();
        event.setResult(rand.nextInt(int1, int2));
    }

    @ReceiveEvent
    public void onEvaluateBlockEvent(EvaluateBlockEvent event, EntityRef entity, ScenarioValueBlockUriComponent component) {
        event.setResult(blockManager.getBlockFamily(component.block_uri));
    }

    @ReceiveEvent
    public void onEvaluateBlockEvent(EvaluateBlockEvent event, EntityRef entity, ScenarioValueTriggeringBlockComponent comp) {
        EntityRef passed = event.getPassedEntity();
        BlockComponent block = passed.getComponent(InfoDestroyedBlockComponent.class).destroyedBlock.getComponent(BlockComponent.class);
        event.setResult(block.getBlock().getBlockFamily());
    }

    @ReceiveEvent
    public void onConditionalCheckEvent(ConditionalCheckEvent event, EntityRef entity, ScenarioSecondaryBlockCompareComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateBlockEvent evalBlock1 = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block1").send(evalBlock1);
        BlockFamily block1 = evalBlock1.getResult();

        EvaluateBlockEvent evalBlock2 = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block2").send(evalBlock2);
        BlockFamily block2 = evalBlock2.getResult();

        event.setResult(block1.equals(block2));
    }

    @ReceiveEvent
    public void onEvaluateItemPrefabEvent(EvaluateItemPrefabEvent event, EntityRef entity, ScenarioValueItemPrefabUriComponent component) {
        event.setResult(prefabManager.getPrefab(component.prefabURI));
    }

    @ReceiveEvent
    public void onEvaluateCountItem(EvaluateIntEvent event, EntityRef entity, ScenarioExpressionItemCountComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        //TODO: Replaced once evaluation of player is done
        InfoTriggeringEntityComponent temp = event.getPassedEntity().getComponent(InfoTriggeringEntityComponent.class);
        InventoryComponent invent = temp.entity.getComponent(InventoryComponent.class);


        EvaluateItemPrefabEvent evalPrefab = new EvaluateItemPrefabEvent(event.getPassedEntity());
        args.get("item").send(evalPrefab);
        Prefab prefab = evalPrefab.getResult();

        int count = 0;
        for (EntityRef e : invent.itemSlots) {
            if (e.exists() && e.getParentPrefab().exists()) {
                if (e.getParentPrefab().equals(prefab)) {
                    count++;
                }
            }

        }

        event.setResult(count);
    }

    @ReceiveEvent //TODO:Fix this, entity doesn't actually have displayname
    public void onEvaluatePlayerName(EvaluateStringEvent event, EntityRef entity, ScenarioExpressionPlayerNameComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        //TODO: Replaced once evaluation of player is done
        InfoTriggeringEntityComponent temp = event.getPassedEntity().getComponent(InfoTriggeringEntityComponent.class);
        EntityRef clientInfo = temp.entity.getOwner().getComponent(ClientComponent.class).clientInfo;
        DisplayNameComponent name = clientInfo.getComponent(DisplayNameComponent.class);

        event.setResult(name.name);
    }

    @ReceiveEvent
    public void onEvaluateComparator(EvaluateComparatorEvent event, EntityRef entity, ScenarioValueComparatorComponent comp) {
        event.setResult(comp.compare);
    }

    @ReceiveEvent
    public void onEvaluateIntComparison(ConditionalCheckEvent event, EntityRef entity, ScenarioSecondaryIntCompareComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateIntEvent evalInt1 = new EvaluateIntEvent(event.getPassedEntity());
        args.get("int1").send(evalInt1);
        int int1 = evalInt1.getResult();

        EvaluateIntEvent evalInt2 = new EvaluateIntEvent(event.getPassedEntity());
        args.get("int2").send(evalInt2);
        int int2 = evalInt2.getResult();

        EvaluateComparatorEvent evalCompare = new EvaluateComparatorEvent(event.getPassedEntity());
        args.get("compare").send(evalCompare);

        event.setResult(evalCompare.getResult().evaluate(int1, int2));
    }

    @ReceiveEvent
    public void onEvaluatePlayerRegionComparison(ConditionalCheckEvent event, EntityRef entity, ScenarioSecondaryPlayerRegionComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateRegionEvent evalRegion = new EvaluateRegionEvent(event.getPassedEntity());
        args.get("region").send(evalRegion);
        EntityRef region = evalRegion.getResult();

        //TODO: Replaced once evaluation of player is done
        EntityRef player = event.getPassedEntity().getComponent(InfoTriggeringEntityComponent.class).entity.getOwner().getComponent(ClientComponent.class).character;
        RegionLocationComponent regionComp = region.getComponent(RegionLocationComponent.class);

        Vector3f loc = player.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());

        event.setResult(regionComp.region.contains((int) loc.x, (int) loc.y, (int) loc.z));
    }

    @ReceiveEvent
    public void onEvaluateRegion(EvaluateRegionEvent event, EntityRef entity, ScenarioValueRegionComponent comp) {
        event.setResult(comp.regionEntity);
    }

    @ReceiveEvent
    public void onEvaluateConcatStringEvent(EvaluateStringEvent event, EntityRef entity, ScenarioExpressionConcatStringComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateStringEvent evalStr1 = new EvaluateStringEvent(event.getPassedEntity());
        args.get("string1").send(evalStr1);
        String str1 = evalStr1.getResult();

        EvaluateStringEvent evalStr2 = new EvaluateStringEvent(event.getPassedEntity());
        args.get("string2").send(evalStr2);
        String str2 = evalStr2.getResult();

        event.setResult(str1 + str2);
    }

    @ReceiveEvent
    public void onEvaluateTriggeringRegion(EvaluateRegionEvent event, EntityRef entity, ScenarioValueTriggeringRegionComponent comp) {
        event.setResult(event.getPassedEntity().getComponent(InfoTriggerRegionComponent.class).region);
    }

    @ReceiveEvent //Name of Region
    public void onEvaluateNameOfRegion(EvaluateStringEvent event, EntityRef entity, ScenarioExpressionRegionNameComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateRegionEvent evalRegion = new EvaluateRegionEvent(event.getPassedEntity());
        args.get("region").send(evalRegion);
        EntityRef region = evalRegion.getResult();

        RegionNameComponent name = region.getComponent(RegionNameComponent.class);
        RegionColorComponent color = region.getComponent(RegionColorComponent.class);

        event.setResult(FontColor.getColored(name.regionName, color.color));
    }

    @ReceiveEvent
    public void onEvaluateCountBlock(EvaluateIntEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        ScenarioValuePlayerComponent.PlayerType player = args.get("player").getComponent(ScenarioValuePlayerComponent.class).type;

        EvaluateBlockEvent evaluateBlockEvent = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block").send(evaluateBlockEvent);
        BlockFamily blockFamily = evaluateBlockEvent.getResult();

        int count = 0;

        if (player == ScenarioValuePlayerComponent.PlayerType.TRIGGERING_PLAYER) {
            EntityRef playerEntity = event.getPassedEntity().getComponent(InfoTriggeringEntityComponent.class).entity;
            InventoryComponent  invent = playerEntity.getComponent(InventoryComponent.class);
            for (EntityRef e : invent.itemSlots) {
                if (e.exists() && e.hasComponent(BlockItemComponent.class)) {
                    if (e.getComponent(BlockItemComponent.class).blockFamily.equals(blockFamily)) {
                        count += e.getComponent(ItemComponent.class).stackCount;
                    }
                }
            }
        }

        event.setResult(count);
    }
}
