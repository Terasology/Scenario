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
package org.terasology.scenario.internal.ui.LogicTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.rendering.assets.texture.TextureRegion;
import org.terasology.scenario.components.ScenarioLogicTextComponent;
import org.terasology.scenario.internal.utilities.ArgumentParser;

/**
 * Value for the logic tree, currently it entails the text to display, the image attached,
 * if it is an event or root (Used to denote which buttons/context options are available),
 * and the entity that is attached to the value allowing for easy reference with out traversing the entity tree structure
 */
public class LogicTreeValue {
    private Logger logger = LoggerFactory.getLogger(LogicTreeValue.class);

    private String text;
    private TextureRegion textureRegion;
    private EntityRef entity;
    private Type valueType;
    private ArgumentParser parser;

    public enum Type {
        SCENARIO,
        TRIGGER,
        EVENT_NAME,
        CONDITIONAL_NAME,
        ACTION_NAME,
        EVENT,
        CONDITIONAL,
        ACTION
    }

    public LogicTreeValue(String text, TextureRegion textureRegion, Type valueType) {
        this.text = text;
        this.textureRegion = textureRegion;
        this.valueType = valueType;
    }

    public LogicTreeValue(String text, TextureRegion textureRegion, Type valueType, EntityRef entity, ArgumentParser parser) {
        this.text = text;
        this.textureRegion = textureRegion;
        this.valueType = valueType;
        this.entity = entity;
        this.parser = parser;
    }

    //Constructor if creating an event, action, or conditional and text should be built upon what the actual action/event is
    public LogicTreeValue(TextureRegion textureRegion, Type valueType, EntityRef entity, ArgumentParser parser) {
        this.textureRegion = textureRegion;
        this.valueType = valueType;
        this.entity = entity;
        this.parser = parser;

        this.text = "Generic trigger";

        //Check for action/event/conditional
        if (valueType == Type.ACTION || valueType == Type.EVENT || valueType == Type.CONDITIONAL) {
            if (entity.hasComponent(ScenarioLogicTextComponent.class)) {
                text = parser.parseDisplayText(entity);
            }
        }
        //Other types are handled with item renderer

    }

    public String getText() {
        return text;
    }

    public Type getValueType() {
        return valueType;
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

}
