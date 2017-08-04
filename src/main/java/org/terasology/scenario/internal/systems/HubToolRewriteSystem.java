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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.scenario.components.ScenarioHubToolUpdateComponent;
import org.terasology.scenario.internal.events.HubtoolRewriteLogicEvent;
import org.terasology.scenario.internal.events.HubtoolRewriteRegionEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class HubToolRewriteSystem extends BaseComponentSystem {
    @ReceiveEvent
    public void onHubtoolRewriteLogicEvent(HubtoolRewriteLogicEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        if (component.localScreen != null) { //Makes sure it is the owner of the hubtool
            component.localScreen.redrawLogic();
        }
    }

    @ReceiveEvent
    public void onHubtoolRewriteRegionEvent(HubtoolRewriteRegionEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        if (component.localScreen != null) { //Makes sure it is the owner of the hubtool
            component.localScreen.redrawRegions();
        }
    }
}
