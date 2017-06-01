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

import org.terasology.rendering.assets.texture.TextureRegion;

public class LogicTreeValue {
    private String text;
    private TextureRegion textureRegion;
    private boolean isEvent;
    private boolean isRoot;

    public LogicTreeValue(String text, boolean isEvent, TextureRegion textureRegion, boolean isRoot) {
        this.text = text;
        this.isEvent = isEvent;
        this.textureRegion = textureRegion;
        this.isRoot = isRoot;
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

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    @Override
    public final String toString() {
        return text + " " + Boolean.toString(isEvent);
    }
}
