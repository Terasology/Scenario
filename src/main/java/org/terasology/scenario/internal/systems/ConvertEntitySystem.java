// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryDamageAmountComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryGiveBlockComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryGiveItemComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryHealAmountComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryLogInfoComponent;
import org.terasology.scenario.components.actions.ScenarioSecondarySendChatComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryTakeBlockComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryTakeItemComponent;
import org.terasology.scenario.components.actions.ScenarioSecondaryTeleportComponent;
import org.terasology.scenario.components.conditionals.ScenarioSecondaryBlockCompareComponent;
import org.terasology.scenario.components.conditionals.ScenarioSecondaryIntCompareComponent;
import org.terasology.scenario.components.conditionals.ScenarioSecondaryPlayerRegionComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryBlockDestroyComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryEnterRegionComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryLeaveRegionComponent;
import org.terasology.scenario.components.events.ScenarioSecondaryRespawnComponent;
import org.terasology.scenario.components.events.ScenarioSecondarySpawnComponent;
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
import org.terasology.scenario.internal.events.ConvertScenarioEntityEvent;

import java.util.Map;

/**
 * System that recursively takes a logic entity and develops a list of serialised strings that allow for recreation of the entity following
 * the same order of the list(Makes sure that the list always goes down in in depth, not a skip of branches that would result in wanting to
 * satisfy an entity that doesn't yet exist
 */
@RegisterSystem(RegisterMode.CLIENT)
public class ConvertEntitySystem extends BaseComponentSystem {

    /**
     * Most entities will follow a similar method, but to leave the system open for future inclusions with new event/action/conditions it is
     * designed with each specific component(the unique component of a logic prefab). All are based off the component that indicates what
     * type an entity is.
     * <p>
     * Follows a pattern of [PREFAB]prefabName{key name for entity argument}[VALUE]value of the component
     */


    private static final String PREFAB_MARKER = "[PREFAB]";
    private static final String VALUE_MARKER = "[VALUE]";

    private final Logger logger = LoggerFactory.getLogger(ConvertEntitySystem.class);

    /**
     * Anything that has an argument container and is not a value would use this serialization
     *
     * @param event the ConvertScenarioEntityEvent that triggered the original call
     * @param entity The entityRef that the call was sent to
     */
    public void defaultSerialize(ConvertScenarioEntityEvent event, EntityRef entity) {
        event.getOutputList().add(event.getPrefix() + PREFAB_MARKER + entity.getParentPrefab().getName());

        for (Map.Entry<String, EntityRef> e : entity.getComponent(ScenarioArgumentContainerComponent.class).arguments.entrySet()) {
            ConvertScenarioEntityEvent newEvent = new ConvertScenarioEntityEvent(event.getPrefix() + "{" + e.getKey() + "}");
            e.getValue().send(newEvent);
            for (String s : newEvent.getOutputList()) {
                event.getOutputList().add(s);
            }
        }
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueTriggeringRegionComponent component) {
        event.getOutputList().add(event.getPrefix() + PREFAB_MARKER + entity.getParentPrefab().getName());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondarySendChatComponent component) {
        defaultSerialize(event, entity);
    }


    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueTriggeringBlockComponent component) {
        event.getOutputList().add(event.getPrefix() + PREFAB_MARKER + entity.getParentPrefab().getName());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioExpressionRandomIntComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioExpressionPlayerNameComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueStringComponent component) {
        event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.string);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueRegionComponent component) {
        if (component.regionEntity.getComponent(NetworkComponent.class).getNetworkId() == 0) {
            //No network component (local/single player, so can't/don't need to use network id)
            event.getOutputList().add(event.getPrefix() + VALUE_MARKER + "x" + component.regionEntity.getId());
        } else {
            event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.regionEntity.getComponent(NetworkComponent.class).getNetworkId());
        }
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueItemPrefabUriComponent component) {
        event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.prefabURI);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueIntegerComponent component) {
        event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.value);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueComparatorComponent component) {
        event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.compare.name());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValueBlockUriComponent component) {
        event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.blockUri);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioValuePlayerComponent component) {
        event.getOutputList().add(event.getPrefix() + VALUE_MARKER + component.type.name());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioExpressionRegionNameComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryPlayerRegionComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondarySpawnComponent component) {
        event.getOutputList().add(event.getPrefix() + PREFAB_MARKER + entity.getParentPrefab().getName());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryRespawnComponent component) {
        event.getOutputList().add(event.getPrefix() + PREFAB_MARKER + entity.getParentPrefab().getName());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryLeaveRegionComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryEnterRegionComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryBlockDestroyComponent component) {
        event.getOutputList().add(event.getPrefix() + PREFAB_MARKER + entity.getParentPrefab().getName());
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryLogInfoComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioExpressionItemCountComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryIntCompareComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryGiveItemComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryGiveBlockComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity,
                                     ScenarioExpressionConcatStringComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryTeleportComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryBlockCompareComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryTakeItemComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryTakeBlockComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryHealAmountComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryDamageAmountComponent component) {
        defaultSerialize(event, entity);
    }

    @ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent component) {
        defaultSerialize(event, entity);
    }
}
