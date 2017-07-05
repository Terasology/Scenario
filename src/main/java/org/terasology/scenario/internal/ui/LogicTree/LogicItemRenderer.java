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

import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.TextLineBuilder;
import org.terasology.rendering.nui.itemRendering.AbstractItemRenderer;
import org.terasology.scenario.internal.ui.LogicTree.LogicTreeValue;

import java.util.List;

/**
 * Renderer for the treeview represention of a logicTreeValue.
 * Currently it doesn't do much, just attach an image prior to the name of the trigger.
 * Will eventually have some complexities for visual help for the user interface and then it will be more usefull.
 */
public class LogicItemRenderer extends AbstractItemRenderer<LogicTreeValue> {
    private final int marginTop;
    private final int marginBottom;
    private final int marginLeft;
    private final int marginRight;

    public LogicItemRenderer() {
        this(2, 2, 5, 5);
    }

    public LogicItemRenderer(int marginTop, int marginBottom, int marginLeft, int marginRight) {
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }

    @Override
    public void draw(LogicTreeValue value, Canvas canvas){
        TextureRegion texture = value.getTextureRegion();

        if (texture != null){
            if (marginTop + texture.getHeight() + marginBottom > canvas.size().y) { // Shrink vertically if it doesn't fit
                int iconHeight = canvas.size().y - marginTop - marginBottom;
                canvas.drawTexture(texture, Rect2i.createFromMinAndSize(marginLeft, marginTop, texture.getWidth(), iconHeight));
            }
            else { // Center Vertically if fit
                int iconVerticalPosition = (canvas.size().y - texture.getHeight()) / 2;
                canvas.drawTexture(texture, Rect2i.createFromMinAndSize(marginLeft, iconVerticalPosition, texture.getWidth(), texture.getHeight()));
            }
        }


        String text;

        // Names and scenarios don't need text, just icon
        switch (value.getValueType()) {
            case ACTION_NAME:
            case CONDITIONAL_NAME:
            case EVENT_NAME:
            case SCENARIO:
                text = "";
                break;
            default:
            case TRIGGER:
            case ACTION:
            case CONDITIONAL:
            case EVENT:
                text = value.getText();
                break;
        }

        int iconWidth;
        if (texture != null){
            iconWidth = marginLeft + texture.getWidth() + marginRight;
        }
        else {
            iconWidth = 0;
        }

        Rect2i textRegion = Rect2i.createFromMinAndSize(iconWidth, 0, canvas.getRegion().width() - iconWidth, canvas.getRegion().height());
        canvas.drawText(text, textRegion);
    }

    @Override
    public Vector2i getPreferredSize(LogicTreeValue value, Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        String text;

        // Names and scenarios don't need text, just icon
        switch (value.getValueType()) {
            case ACTION_NAME:
            case CONDITIONAL_NAME:
            case EVENT_NAME:
            case SCENARIO:
                text = "";
                break;
            default:
            case TRIGGER:
            case ACTION:
            case CONDITIONAL:
            case EVENT:
                text = value.getText();
                break;
        }

        TextureRegion texture = value.getTextureRegion();
        if (texture != null) {
            List<String> lines = TextLineBuilder.getLines(font, text, canvas.size().x);
            return font.getSize(lines);
        }
        else {
            int iconWidth = marginLeft + texture.getWidth() + marginRight;
            List<String> lines = TextLineBuilder.getLines(font, text, canvas.size().x - iconWidth);
            return font.getSize(lines).addX(iconWidth);
        }
    }
}
