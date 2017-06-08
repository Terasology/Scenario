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
package org.terasology.scenario.internal.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.scenario.components.ActionComponent;
import org.terasology.scenario.components.EventTypeComponent;
import org.terasology.world.block.BlockManager;


/**
 * Value for the logic tree, currently it entails the text to display, the image attached,
 * if it is an event or root (Used to denote which buttons/context options are available),
 * and the entity that is attached to the value allowing for easy reference with out traversing the entity tree structure
 */
public class LogicTreeValue {
    private Logger logger = LoggerFactory.getLogger(LogicTreeValue.class);

    private String text;
    private TextureRegion textureRegion;
    private boolean isEvent;
    private boolean isRoot;
    private EntityRef entity;

    public LogicTreeValue(String text, boolean isEvent, TextureRegion textureRegion, boolean isRoot) {
        this.text = text;
        this.isEvent = isEvent;
        this.textureRegion = textureRegion;
        this.isRoot = isRoot;
    }

    public LogicTreeValue(String text, boolean isEvent, TextureRegion textureRegion, boolean isRoot, EntityRef entity) {
        this.text = text;
        this.isEvent = isEvent;
        this.textureRegion = textureRegion;
        this.isRoot = isRoot;
        this.entity = entity;
    }

    //Constructor if creating an event or an action and text should be built upon what the actual action/event is
    public LogicTreeValue(boolean isEvent, TextureRegion textureRegion, boolean isRoot, EntityRef entity) {
        this.isEvent = isEvent;
        this.textureRegion = textureRegion;
        this.isRoot = isRoot;
        this.entity = entity;

        this.text = "Generic trigger";

        //Check for action
        if (entity.hasComponent(ActionComponent.class)) {
            ActionComponent comp = entity.getComponent(ActionComponent.class);
            switch (comp.type) {
                case GIVE_ITEM:
                    text = "Give player " + comp.numItems + " " + comp.itemIdName;
                    break;
                default:
                    break;
            }
        }

        //Check for event
        if (entity.hasComponent(EventTypeComponent.class)) {
            EventTypeComponent comp = entity.getComponent(EventTypeComponent.class);
            switch (comp.type) {
                case PLAYER_SPAWN:
                    text = "On player spawn";
                    break;
                default:
                    break;
            }
        }

    }

    public String getText() {
        return text;
    }

    public boolean isEvent(){
        return isEvent;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setEntity (EntityRef entity) {
        this.entity = entity;
    }

    public EntityRef getEntity() {
        return entity;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    @Override
    public final String toString() {
        return text + " " + Boolean.toString(isEvent);
    }
}
