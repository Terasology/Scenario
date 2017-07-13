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

@RegisterSystem(RegisterMode.AUTHORITY)
public class RegionSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;

    @In
    private PrefabManager prefabManager;

    @In
    private AssetManager assetManager;

    private Logger logger = LoggerFactory.getLogger(RegionSystem.class);

    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void onAttackEntity(AttackEvent event, EntityRef targetEntity, org.terasology.world.block.BlockComponent blockComponent) {
        Iterator<EntityRef> entities = entityManager.getEntitiesWith(RegionBeingCreatedComponent.class).iterator();
        if (entities.hasNext()) {
            if (event.getDirectCause().getParentPrefab() != null && event.getDirectCause().getParentPrefab().equals(assetManager.getAsset("scenario:hubtool", Prefab.class).get())) {
                EntityRef editedRegion = entities.next(); //SHOULD only have one region that can be edited for now, will have to change this in future with multiplayer
                RegionBeingCreatedComponent create = editedRegion.getComponent(RegionBeingCreatedComponent.class);
                Vector3i pos = blockComponent.getPosition();
                if (create.firstHit == null) {
                    create.firstHit = pos;

                    DisplayNameComponent name = new DisplayNameComponent();
                    name.name = "Scenario System";
                    ColorComponent color = new ColorComponent();
                    color.color = Color.RED;
                    EntityRef ent = entityManager.create(name, color);


                    EntityRef clientInfo = event.getInstigator().getOwner().getComponent(ClientComponent.class).clientInfo;
                    String displayName = FontColor.getColored(clientInfo.getComponent(DisplayNameComponent.class).name, clientInfo.getComponent(ColorComponent.class).color);

                    for (EntityRef client : entityManager.getEntitiesWith(ClientComponent.class)) {
                        client.send(new ChatMessageEvent("Region start registered by " + displayName, ent));
                    }

                    ent.destroy();

                    event.consume();
                }
                else {
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
        }
    }

}
