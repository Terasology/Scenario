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
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.In;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.TextLineBuilder;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.itemRendering.AbstractItemRenderer;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.widgets.UIDropdownScrollable;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.scenario.components.ShortNameComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.ConstStringComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioIntegerComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioStringComponent;

import java.util.ArrayList;
import java.util.List;

public class EditParameterScreen extends CoreScreenLayer {
    public static final ResourceUrn ASSET_URI = new ResourceUrn("scenario:editParameterScreen");

    private static final Logger logger = LoggerFactory.getLogger(EditParameterScreen.class);

    @In
    PrefabManager prefabManager;

    @In
    EntityManager entityManager;

    private EditLogicScreen returnScreen;
    private String key;
    private EntityRef baseEntity;
    private EntityRef tempEntity;

    private ColumnLayout variables;
    private UILabel editLabel;
    private UILabel selectionLabel;
    private UIDropdownScrollable dropdown;
    private List<Prefab> optionList;
    private Prefab selectedPrefab;

    private UIText textEntry;

    //Temporary variable
    private boolean isInt;

    @Override
    public void initialise() {
        variables = find("variableWidget", ColumnLayout.class);
        editLabel = find("editLabel", UILabel.class);
        selectionLabel = find("selectLabel", UILabel.class);
        dropdown = find ("selectionDropdown", UIDropdownScrollable.class);

        WidgetUtil.trySubscribe(this, "okButton", this::onOkButton);
        WidgetUtil.trySubscribe(this, "cancelButton", this::onCancelButton);
    }

    public void setupParameter(String key, EntityRef entity, EditLogicScreen returnScreen) {
        this.key = key;
        this.baseEntity = entity;
        this.tempEntity = baseEntity.copy();
        this.returnScreen = returnScreen;

        optionList = new ArrayList<>();
        if (entity.hasComponent(ScenarioIntegerComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioIntegerComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Integer");
            selectionLabel.setText("Select an Integer type");
        }
        else if (entity.hasComponent(ScenarioStringComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioStringComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit String");
            selectionLabel.setText("Select a String type");
        }

        dropdown.setOptions(optionList);
        setPrefabStart(entity);
        dropdown.setSelection(entity.getParentPrefab());
        dropdown.setOptionRenderer(new AbstractItemRenderer<Prefab>() {
            @Override
            public void draw(Prefab value, Canvas canvas) {
                Rect2i textRegion = Rect2i.createFromMinAndSize(0, 0, canvas.getRegion().width(), canvas.getRegion().height());
                canvas.drawText(value.getComponent(ShortNameComponent.class).name, textRegion);
            }

            @Override
            public Vector2i getPreferredSize(Prefab value, Canvas canvas) {
                Font font = canvas.getCurrentStyle().getFont();
                List<String> lines = TextLineBuilder.getLines(font, value.getComponent(ShortNameComponent.class).name, canvas.size().x);
                return font.getSize(lines);
            }
        });
        dropdown.bindSelection(new Binding<Prefab>() {
            @Override
            public Prefab get() {
                return selectedPrefab;
            }

            @Override
            public void set(Prefab value) {
                if (!selectedPrefab.equals(value)) {
                    setPrefab(value);
                }
            }
        });
    }

    private void onOkButton(UIWidget button) {
        if (isInt) {
            tempEntity.getComponent(ConstIntegerComponent.class).value = Integer.parseInt(textEntry.getText());
            tempEntity.saveComponent(tempEntity.getComponent(ConstIntegerComponent.class));
        }
        else {
            tempEntity.getComponent(ConstStringComponent.class).string = textEntry.getText();
            tempEntity.saveComponent(tempEntity.getComponent(ConstStringComponent.class));
        }
        if (!tempEntity.equals(baseEntity)) {
            returnScreen.setVariable(key, tempEntity);
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        if (tempEntity.exists()) {
            tempEntity.destroy();
        }
        getManager().popScreen();
    }

    private void setPrefab(Prefab value) {
        selectedPrefab = value;
        tempEntity = entityManager.create(value);

        setupInteraction();
    }

    private void setPrefabStart(EntityRef entity) {
        selectedPrefab = entity.getParentPrefab();
        tempEntity = entity.copy();
        setupInteraction();
    }

    private void setupInteraction() {
        if (tempEntity.hasComponent(ConstIntegerComponent.class)) {
            String entryValue;
            UIText entry = new UIText();
            entry.setReadOnly(false);
            entryValue = Integer.toString(tempEntity.getComponent(ConstIntegerComponent.class).value);
            entry.setText(entryValue);
            if (textEntry != null) {
                variables.removeWidget(textEntry);
            }
            textEntry = entry;
            variables.addWidget(entry);
            isInt = true;
        }
        else { //Assuming only string or int right now
            UIText entry = new UIText();
            entry.setReadOnly(false);
            String entryValue;
            entryValue = tempEntity.getComponent(ConstStringComponent.class).string;
            entry.setText(entryValue);
            if (textEntry != null) {
                variables.removeWidget(textEntry);
            }
            textEntry = entry;
            variables.addWidget(entry);
            isInt = false;
        }
    }


}
