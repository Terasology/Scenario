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
package org.terasology.scenario.internal.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.events.AttackEvent;
import org.terasology.logic.chat.ChatMessageEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.network.ClientComponent;
import org.terasology.network.ColorComponent;
import org.terasology.registry.In;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.regions.RegionBeingCreatedComponent;
import org.terasology.scenario.components.regions.RegionContainingEntitiesComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.internal.events.RegionTreeFullAddEvent;

import java.util.Iterator;

/**
 * System that monitors attack hits and consumes them if they are being used to create the region of a scenario region entity.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @In
    private PrefabManager prefabManager;

    @In
    private AssetManager assetManager;

    private Logger logger = LoggerFactory.getLogger(RegionSystem.class);

    private EntityRef chatMessageEntity;

    /**
     * Setting up the entity that is used to send chat messages (Red-colored "Scenario System")
     */
    @Override
    public void postBegin() {
        chatMessageEntity = entityManager.create(assetManager.getAsset("scenario:scenarioChatEntity", Prefab.class).get());
        chatMessageEntity.getComponent(DisplayNameComponent.class).name = "Scenario System";
        chatMessageEntity.saveComponent(chatMessageEntity.getComponent(DisplayNameComponent.class));
        chatMessageEntity.getComponent(ColorComponent.class).color = Color.RED;
        chatMessageEntity.saveComponent(chatMessageEntity.getComponent(ColorComponent.class));
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void onAttackEntity(AttackEvent event, EntityRef targetEntity, org.terasology.world.block.BlockComponent blockComponent) {
        Iterator<EntityRef> entities = entityManager.getEntitiesWith(RegionBeingCreatedComponent.class).iterator();
        while (entities.hasNext()) {
            EntityRef editedRegion = entities.next();
            if (editedRegion.getComponent(RegionBeingCreatedComponent.class).creatingEntity.equals(event.getInstigator())) {
                if (event.getDirectCause().getParentPrefab() != null && event.getDirectCause().getParentPrefab().equals(assetManager.getAsset("scenario:hubtool", Prefab.class).get())) {
                    RegionBeingCreatedComponent create = editedRegion.getComponent(RegionBeingCreatedComponent.class);
                    Vector3i pos = blockComponent.getPosition();
                    if (create.firstHit == null) {
                        create.firstHit = pos;

                        event.getInstigator().getOwner().send(new ChatMessageEvent("Region started, left click next location", chatMessageEntity));

                        event.consume();
                    } else {
                        if (!pos.equals(create.firstHit)) {
                            RegionLocationComponent loc = editedRegion.getComponent(RegionLocationComponent.class);
                            loc.region = Region3i.createBounded(pos, create.firstHit);
                            editedRegion.saveComponent(loc);
                            editedRegion.removeComponent(RegionBeingCreatedComponent.class);
                            if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
                                EntityRef scenario = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
                                if (scenario == null) {
                                    return;
                                }
                                scenario.send(new RegionTreeFullAddEvent(editedRegion, event.getInstigator()));
                            }
                            event.consume();
                        }
                    }

                }
                break; //Don't need to check any more regions if one already matched(only one region is allowed per person at a time)
            }
        }
    }

}
