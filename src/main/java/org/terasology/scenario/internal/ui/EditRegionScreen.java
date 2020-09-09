// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.ui;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.math.DoubleMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.assets.texture.TextureUtil;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.math.geom.Vector3i;
import org.terasology.nui.Color;
import org.terasology.nui.UIWidget;
import org.terasology.nui.WidgetUtil;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.widgets.UICheckbox;
import org.terasology.nui.widgets.UIImage;
import org.terasology.nui.widgets.UISlider;
import org.terasology.nui.widgets.UIText;
import org.terasology.scenario.components.ScenarioRegionVisibilityComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionLocationComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.events.RegionProtectEvent;
import org.terasology.scenario.internal.events.RegionRecolorEvent;
import org.terasology.scenario.internal.events.RegionRenameEvent;
import org.terasology.scenario.internal.events.RegionResizeEvent;
import org.terasology.scenario.internal.events.RegionTeleportationRequestEvent;
import org.terasology.scenario.internal.utilities.CieCamColorsScenario;
import org.terasology.structureTemplates.components.ProtectedRegionsComponent;

import java.math.RoundingMode;
import java.util.List;

/**
 * Screen displayed for editing a region, all changes are made client side and when the "OK" button is pressed the
 * changed values are sent to the server using various events to alter the region entity on the server's side. The color
 * display of the region utilizes a slider and box to generate the color, the rest are very simplistic widgets(number or
 * text entries or checkboxes)
 */
public class EditRegionScreen extends CoreScreenLayer {
    public static final ResourceUrn ASSET_URI = new ResourceUrn("scenario:editRegionScreen!instance");

    private static final Logger logger = LoggerFactory.getLogger(EditRegionScreen.class);
    private final List<Color> colors = CieCamColorsScenario.L65C65;
    @In
    EntityManager entityManager;
    private HubToolScreen returnScreen;
    private EntityRef baseEntity;
    private UIText nameEntry;
    private UICheckbox visiblity;
    private UICheckbox protectedRegion;
    private UISlider colorSlider;
    private UIImage colorImage;
    private UIText minXField;
    private UIText minYField;
    private UIText minZField;
    private UIText sizeXField;
    private UIText sizeYField;
    private UIText sizeZField;
    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {
        nameEntry = find("nameEntry", UIText.class);
        visiblity = find("visibility", UICheckbox.class);
        protectedRegion = find("protected", UICheckbox.class);
        colorSlider = find("colorSlider", UISlider.class);
        colorImage = find("colorImage", UIImage.class);

        minXField = find("minX", UIText.class);
        minYField = find("minY", UIText.class);
        minZField = find("minZ", UIText.class);

        sizeXField = find("sizeX", UIText.class);
        sizeYField = find("sizeY", UIText.class);
        sizeZField = find("sizeZ", UIText.class);

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
        protectedRegion.setChecked(entity.hasComponent(ProtectedRegionsComponent.class));

        Region3i region = entity.getComponent(RegionLocationComponent.class).region;

        minXField.setText(Integer.toString(region.minX()));
        minYField.setText(Integer.toString(region.minY()));
        minZField.setText(Integer.toString(region.minZ()));
        sizeXField.setText(Integer.toString(region.sizeX()));
        sizeYField.setText(Integer.toString(region.sizeY()));
        sizeZField.setText(Integer.toString(region.sizeZ()));

        if (colorSlider != null) {
            Color color = entity.getComponent(RegionColorComponent.class).color;
            colorSlider.bindValue(new NotifyingBinding(findClosestIndex(color)));
        }

        updateImage();
    }

    public void onOkButton(UIWidget button) {
        returnScreen.getEntity().send(new RegionResizeEvent(baseEntity, getRegion()));

        if (!nameEntry.getText().equals(baseEntity.getComponent(RegionNameComponent.class).regionName)) {
            returnScreen.getEntity().send(new RegionRenameEvent(baseEntity, nameEntry.getText()));
        }
        if (!colorImage.getTint().equals(baseEntity.getComponent(RegionColorComponent.class).color)) {
            returnScreen.getEntity().send(new RegionRecolorEvent(baseEntity, colorImage.getTint()));
        }
        if (visiblity.isChecked()) {
            ScenarioRegionVisibilityComponent vis =
                    returnScreen.getEntity().getOwner().getComponent(ScenarioRegionVisibilityComponent.class);
            vis.visibleList.add(baseEntity);
            returnScreen.getEntity().getOwner().saveComponent(vis);
        } else {
            ScenarioRegionVisibilityComponent vis =
                    returnScreen.getEntity().getOwner().getComponent(ScenarioRegionVisibilityComponent.class);
            vis.visibleList.remove(baseEntity);
            returnScreen.getEntity().getOwner().saveComponent(vis);
        }
        returnScreen.getEntity().send(new RegionProtectEvent(baseEntity, protectedRegion.isChecked()));

        getManager().popScreen();
    }

    public void onCancelButton(UIWidget button) {
        getManager().popScreen();
    }

    public void onTeleportButton(UIWidget button) {
        returnScreen.getEntity().send(new RegionTeleportationRequestEvent(localPlayer.getCharacterEntity(),
                baseEntity));
    }

    public Region3i getRegion() {
        return Region3i.createFromMinAndSize(
                new Vector3i(integerFromField(minXField), integerFromField(minYField), integerFromField(minZField)),
                new Vector3i(integerFromField(sizeXField), integerFromField(sizeYField), integerFromField(sizeZField))
        );
    }

    private Integer integerFromField(UIText field) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public boolean isLowerLayerVisible() {
        return false;
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
        } else {
            return baseEntity.getComponent(RegionColorComponent.class).color;
        }
    }

    private Color findClosestColor(float findex) {
        int index = DoubleMath.roundToInt(findex * (colors.size() - 1), RoundingMode.HALF_UP);
        Color color = colors.get(index);
        return color;
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
}
