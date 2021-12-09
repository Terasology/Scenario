// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui.RegionTree;

import org.joml.Vector2i;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.FontColor;
import org.terasology.nui.TextLineBuilder;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.itemRendering.AbstractItemRenderer;
import org.terasology.nui.util.RectUtility;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;

import java.util.List;

/**
 * New item renderer required because it must pull all of the data from components that the normal render would not know about in order to
 * generate the name for the region and the coloring
 */
public class RegionItemRenderer extends AbstractItemRenderer<RegionTreeValue> {
    private final int marginTop;
    private final int marginBottom;
    private final int marginLeft;
    private final int marginRight;

    public RegionItemRenderer() {
        this(2, 2, 5, 5);
    }

    public RegionItemRenderer(int marginTop, int marginBottom, int marginLeft, int marginRight) {
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }

    @Override
    public void draw(RegionTreeValue value, Canvas canvas) {
        String text;

        if (value.getEntity() == null) {
            text = FontColor.getColored("Regions", Color.WHITE);
        } else {
            text = FontColor.getColored(value.getEntity().getComponent(RegionNameComponent.class).regionName,
                    value.getEntity().getComponent(RegionColorComponent.class).color);
        }

        Rectanglei textRegion = RectUtility.createFromMinAndSize(0, 0, canvas.getRegion().lengthX(), canvas.getRegion().lengthY());
        canvas.drawText(text, textRegion);

    }

    @Override
    public Vector2i getPreferredSize(RegionTreeValue value, Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        String text;

        if (value.getEntity() == null) {
            text = "Regions";
        } else {
            text = value.getEntity().getComponent(RegionNameComponent.class).regionName;
        }

        List<String> lines = TextLineBuilder.getLines(font, text, canvas.size().x);
        return font.getSize(lines);
    }
}
