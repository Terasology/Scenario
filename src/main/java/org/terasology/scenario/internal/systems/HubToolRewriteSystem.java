// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
    public void onHubtoolRewriteLogicEvent(HubtoolRewriteLogicEvent event, EntityRef entity,
                                           ScenarioHubToolUpdateComponent component) {
        if (component.localScreenID != null) { //Makes sure it is the owner of the hubtool
            if (nuiManager.getScreen(component.localScreenID) != null) {
                ((HubToolScreen) nuiManager.getScreen(component.localScreenID)).redrawLogic();
            }
        }
    }

    @ReceiveEvent
    public void onHubtoolRewriteRegionEvent(HubtoolRewriteRegionEvent event, EntityRef entity,
                                            ScenarioHubToolUpdateComponent component) {
        if (component.localScreenID != null) { //Makes sure it is the owner of the hubtool
            if (nuiManager.getScreen(component.localScreenID) != null) {
                ((HubToolScreen) nuiManager.getScreen(component.localScreenID)).redrawRegions();
            }
        }
    }
}
