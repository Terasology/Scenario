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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.math.DoubleMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.widgets.UICheckbox;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.rendering.nui.widgets.UISlider;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.scenario.components.ScenarioRegionVisibilityComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.RegionRecolorEvent;
import org.terasology.scenario.internal.events.RegionRenameEvent;
import org.terasology.scenario.internal.utilities.CieCamColorsScenario;
import org.terasology.utilities.Assets;

import java.math.RoundingMode;
import java.util.List;

public class EditRegionScreen extends CoreScreenLayer {
    public static final ResourceUrn ASSET_URI = new ResourceUrn("scenario:editRegionScreen!instance");

    private static final Logger logger = LoggerFactory.getLogger(EditRegionScreen.class);

    @In
    EntityManager entityManager;

    private HubToolScreen returnScreen;
    private EntityRef baseEntity;

    private UIText nameEntry;
    private UICheckbox visiblity;
    private UISlider colorSlider;
    private UIImage colorImage;

    @In
    private LocalPlayer localPlayer;


    private final List<Color> colors = CieCamColorsScenario.L65C65;

    @Override
    public void initialise() {
        nameEntry = find("nameEntry", UIText.class);
        visiblity = find("visibility", UICheckbox.class);
        colorSlider = find("colorSlider", UISlider.class);
        colorImage = find("colorImage", UIImage.class);

        colorSlider.setIncrement(0.01f);
        Function<Object, String> constant = Functions.constant("  ");   // ensure a certain width
        colorSlider.setLabelFunction(constant);

        if (colorImage != null) {
            ResourceUrn uri = TextureUtil.getTextureUriForColor(Color.WHITE);
            Texture tex = Assets.get(uri, Texture.class).get();
            colorImage.setImage(tex);
        }

        WidgetUtil.trySubscribe(this, "okButton", this::onOkButton);
        WidgetUtil.trySubscribe(this, "cancelButton", this::onCancelButton);
        WidgetUtil.trySubscribe(this, "teleportButton", this::onTeleportButton);
    }

    public void setupDisplay(EntityRef entity, HubToolScreen returnScreen) {
        this.baseEntity = entity;
        this.returnScreen = returnScreen;

        nameEntry.setText(entity.getComponent(RegionNameComponent.class).regionName);
        visiblity.setChecked(returnScreen.getEntity().getOwner().getComponent(ScenarioRegionVisibilityComponent.class).visibleList.contains(entity));

        if (colorSlider != null) {
            Color color = entity.getComponent(RegionColorComponent.class).color;
            colorSlider.bindValue(new NotifyingBinding(findClosestIndex(color)));
        }

        updateImage();
    }

    public void onOkButton(UIWidget button) {
        if (!nameEntry.getText().equals(baseEntity.getComponent(RegionNameComponent.class).regionName)) {
            returnScreen.getEntity().send(new RegionRenameEvent(baseEntity, nameEntry.getText()));
        }
        if (!colorImage.getTint().equals(baseEntity.getComponent(RegionColorComponent.class).color)) {
            returnScreen.getEntity().send(new RegionRecolorEvent(baseEntity, colorImage.getTint()));
        }
        if (visiblity.isChecked()) {
            ScenarioRegionVisibilityComponent vis = returnScreen.getEntity().getOwner().getComponent(ScenarioRegionVisibilityComponent.class);
            vis.visibleList.add(baseEntity);
            returnScreen.getEntity().getOwner().saveComponent(vis);
        }
        else {
            ScenarioRegionVisibilityComponent vis = returnScreen.getEntity().getOwner().getComponent(ScenarioRegionVisibilityComponent.class);
            vis.visibleList.remove(baseEntity);
            returnScreen.getEntity().getOwner().saveComponent(vis);
        }
        getManager().popScreen();
    }

    public void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

    public void onTeleportButton(UIWidget button) {
        org.terasology.math.geom.Vector3f location = baseEntity.getComponent(RegionLocationComponent.class).region.center();
        CharacterTeleportEvent tele = new CharacterTeleportEvent(location);
        localPlayer.getCharacterEntity().send(tele);
    }

    @Override
    public boolean isLowerLayerVisible() {
        return false;
    }

    /**
     * Calls update() in parent class when the slider value changes
     */
    private final class NotifyingBinding extends DefaultBinding<Float> {

        private NotifyingBinding(Float value) {
            super(value);
        }

        @Override
        public void set(Float v) {
            super.set(v);

            updateImage();
        }
    }

    private float findClosestIndex(Color color) {
        int best = 0;
        float minDist = Float.MAX_VALUE;
        for (int i = 0; i < colors.size(); i++) {
            Color other = colors.get(i);
            float dr = other.rf() - color.rf();
            float dg = other.gf() - color.gf();
            float db = other.bf() - color.bf();

            // there are certainly smarter ways to measure color distance,
            // but Euclidean distance is good enough for the purpose
            float dist = dr * dr + dg * dg + db * db;
            if (dist < minDist) {
                minDist = dist;
                best = i;
            }
        }

        float max = colors.size() - 1;
        return best / max;
    }

    private void updateImage() {
        Color color = getColor();
        if (colorImage != null) {
            colorImage.setTint(color);
        }
    }

    private Color getColor() {
        if (colorSlider != null) {
            float index = colorSlider.getValue();
            return findClosestColor(index);
        }
        else {
            return baseEntity.getComponent(RegionColorComponent.class).color;
        }
    }

    private Color findClosestColor(float findex) {
        int index = DoubleMath.roundToInt(findex * (colors.size() - 1), RoundingMode.HALF_UP);
        Color color = colors.get(index);
        return color;
    }
}
