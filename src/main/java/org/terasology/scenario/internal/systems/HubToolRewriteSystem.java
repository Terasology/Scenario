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

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.scenario.components.ScenarioHubToolUpdateComponent;
import org.terasology.scenario.internal.events.HubtoolRewriteLogicEvent;
import org.terasology.scenario.internal.events.HubtoolRewriteRegionEvent;
import org.terasology.scenario.internal.ui.HubToolScreen;

@RegisterSystem(RegisterMode.CLIENT)
public class HubToolRewriteSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;

    @ReceiveEvent
    public void onHubtoolRewriteLogicEvent(HubtoolRewriteLogicEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        if (component.localScreenID != null) { //Makes sure it is the owner of the hubtool
            if (nuiManager.getScreen(component.localScreenID) != null) {
                ((HubToolScreen) nuiManager.getScreen(component.localScreenID)).redrawLogic();
            }
        }
    }

    @ReceiveEvent
    public void onHubtoolRewriteRegionEvent(HubtoolRewriteRegionEvent event, EntityRef entity, ScenarioHubToolUpdateComponent component) {
        if (component.localScreenID != null) { //Makes sure it is the owner of the hubtool
            if (nuiManager.getScreen(component.localScreenID) != null) {
                ((HubToolScreen) nuiManager.getScreen(component.localScreenID)).redrawRegions();
            }
        }
    }
}
