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
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.stubs.OwnerComponent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.conditionals.BlockConditionalComponent;
import org.terasology.scenario.components.conditionals.IntComparisonConditionalComponent;
import org.terasology.scenario.components.events.triggerInformation.DestroyedBlockComponent;
import org.terasology.scenario.components.events.triggerInformation.TriggeringEntityComponent;
import org.terasology.scenario.components.information.ConstBlockComponent;
import org.terasology.scenario.components.information.ConstComparatorComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.ConstItemPrefabComponent;
import org.terasology.scenario.components.information.ConstStringComponent;
import org.terasology.scenario.components.information.ItemCountComponent;
import org.terasology.scenario.components.information.PlayerNameComponent;
import org.terasology.scenario.components.information.RandomIntComponent;
import org.terasology.scenario.components.information.TriggeringBlockComponent;
import org.terasology.scenario.internal.events.evaluationEvents.ConditionalCheckEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateBlockEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateComparatorEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateIntEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateItemPrefabEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateStringEvent;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;

import java.util.Map;

/**
 * Currently just 1 system, will be split up if there becomes too many events that warrant separation by type
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class EvaluationSystem extends BaseComponentSystem {

    private Logger logger = LoggerFactory.getLogger(EvaluationSystem.class);

    @In
    BlockManager blockManager;

    @In
    PrefabManager prefabManager;

    @ReceiveEvent
    public void onEvaluateIntEvent(EvaluateIntEvent event, EntityRef entity, ConstIntegerComponent component) {
        event.setResult(component.value);
    }

    @ReceiveEvent
    public void onEvaluateStringEvent(EvaluateStringEvent event, EntityRef entity, ConstStringComponent component) {
        event.setResult(component.string);
    }

    @ReceiveEvent
    public void onEvaluateIntEvent(EvaluateIntEvent event, EntityRef entity, RandomIntComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

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
    public void onEvaluateBlockEvent(EvaluateBlockEvent event, EntityRef entity, ConstBlockComponent component) {
        event.setResult(blockManager.getBlockFamily(component.block_uri));
    }

    @ReceiveEvent
    public void onEvaluateBlockEvent(EvaluateBlockEvent event, EntityRef entity, TriggeringBlockComponent comp) {
        EntityRef passed = event.getPassedEntity();
        BlockComponent block = passed.getComponent(DestroyedBlockComponent.class).destroyedBlock.getComponent(BlockComponent.class);
        event.setResult(block.getBlock().getBlockFamily());
    }

    @ReceiveEvent
    public void onConditionalCheckEvent(ConditionalCheckEvent event, EntityRef entity, BlockConditionalComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        EvaluateBlockEvent evalBlock1 = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block1").send(evalBlock1);
        BlockFamily block1 = evalBlock1.getResult();

        EvaluateBlockEvent evalBlock2 = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block2").send(evalBlock2);
        BlockFamily block2 = evalBlock2.getResult();

        event.setResult(block1.equals(block2));
    }

    @ReceiveEvent
    public void onEvaluateItemPrefabEvent(EvaluateItemPrefabEvent event, EntityRef entity, ConstItemPrefabComponent component) {
        event.setResult(prefabManager.getPrefab(component.prefabURI));
    }

    @ReceiveEvent
    public void onEvaluateCountItem(EvaluateIntEvent event, EntityRef entity, ItemCountComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        //TODO: Replaced once evaluation of player is done
        TriggeringEntityComponent temp = event.getPassedEntity().getComponent(TriggeringEntityComponent.class);
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
    public void onEvaluatePlayerName(EvaluateStringEvent event, EntityRef entity, PlayerNameComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

        //TODO: Replaced once evaluation of player is done
        TriggeringEntityComponent temp = event.getPassedEntity().getComponent(TriggeringEntityComponent.class);
        EntityRef clientInfo = temp.entity.getOwner().getComponent(ClientComponent.class).clientInfo;
        DisplayNameComponent name = clientInfo.getComponent(DisplayNameComponent.class);

        event.setResult(name.name);
    }

    @ReceiveEvent
    public void onEvaluateComparator(EvaluateComparatorEvent event, EntityRef entity, ConstComparatorComponent comp) {
        event.setResult(comp.compare);
    }

    @ReceiveEvent
    public void onEvaluateIntComparison(ConditionalCheckEvent event, EntityRef entity, IntComparisonConditionalComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ArgumentContainerComponent.class).arguments;

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
}
