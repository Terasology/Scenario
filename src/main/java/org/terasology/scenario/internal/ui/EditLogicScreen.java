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
import org.terasology.scenario.components.ShortNameComponent;
import org.terasology.scenario.components.actions.ActionComponent;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.conditionals.ConditionalComponent;
import org.terasology.scenario.components.events.EventComponent;
import org.terasology.scenario.internal.events.ReplaceEntityEvent;
import org.terasology.scenario.internal.ui.LogicTree.LogicTreeValue;
import org.terasology.scenario.internal.utilities.ArgumentParser;

import java.util.ArrayList;
import java.util.List;

public class EditLogicScreen extends CoreScreenLayer {
    public static final ResourceUrn ASSET_URI = new ResourceUrn("scenario:editLogicScreen!instance");
    private static Logger logger = LoggerFactory.getLogger(EditLogicScreen.class);

    @In
    PrefabManager prefabManager;

    @In
    EntityManager entityManager;

    private EntityRef scenarioEntity;
    private EntityRef targetEntity;
    private EntityRef temporaryEntity;
    private HubToolScreen hubtool;

    private List<UIWidget> oldWidgets;

    private ColumnLayout variables;
    private UILabel editLabel;
    private UILabel selectionLabel;
    private UIDropdownScrollable dropdown;
    private List<Prefab> optionList;
    private Prefab selectedPrefab;

    private ArgumentParser parser;


    @Override
    public void initialise() {
        variables = find("variableWidget", ColumnLayout.class);
        editLabel = find("editLabel", UILabel.class);
        selectionLabel = find("selectLabel", UILabel.class);
        dropdown = find ("selectionDropdown", UIDropdownScrollable.class);



        WidgetUtil.trySubscribe(this, "okButton", this::onOkButton);
        WidgetUtil.trySubscribe(this, "cancelButton", this::onCancelButton);
    }


    public void setEntities(EntityRef scenarioEntity, EntityRef targetEntity, LogicTreeValue.Type type, HubToolScreen hubtool, ArgumentParser parser){
        this.scenarioEntity = scenarioEntity;
        this.targetEntity = targetEntity;
        this.hubtool = hubtool;
        this.parser = parser;

        switch (type) {
            case EVENT:
                editLabel.setText("Edit Event");
                selectionLabel.setText("Select Event");
                break;
            case ACTION:
                editLabel.setText("Edit Action");
                selectionLabel.setText("Select Action");
                break;
            case CONDITIONAL:
                editLabel.setText("Edit Conditon");
                selectionLabel.setText("Select Condition");
                break;
        }

        optionList = getPrefabs(targetEntity);

        dropdown.setOptions(optionList);
        setPrefabStart(targetEntity);
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
        if (!temporaryEntity.equals(targetEntity)) {
            ReplaceEntityEvent event = new ReplaceEntityEvent(targetEntity, temporaryEntity);
            scenarioEntity.send(event);
        }
        else {
            if (temporaryEntity.exists()) {
                temporaryEntity.destroy();
            }
        }
        getManager().popScreen();
    }

    private void onCancelButton(UIWidget button) {
        if (temporaryEntity.exists()) {
            temporaryEntity.destroy();
        }
        getManager().popScreen();
    }

    private List<Prefab> getPrefabs(EntityRef targetEntity) {
        List<Prefab> output = new ArrayList<>();
        if (targetEntity.hasComponent(ActionComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ActionComponent.class);
            for (Prefab p : prefabs) {
                output.add(p);
            }
        }
        if (targetEntity.hasComponent(EventComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(EventComponent.class);
            for (Prefab p : prefabs) {
                output.add(p);
            }
        }
        if (targetEntity.hasComponent(ConditionalComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ConditionalComponent.class);
            for (Prefab p : prefabs) {
                output.add(p);
            }
        }

        return output;
    }

    private void setPrefabStart(EntityRef entity) {
        selectedPrefab = entity.getParentPrefab();

        temporaryEntity = entity.copy();
        generateText();
    }

    private void setPrefab(Prefab value) {
        selectedPrefab = value;

        temporaryEntity = entityManager.create(value);
        parser.parseDefaults(temporaryEntity);
        generateText();
    }

    private void generateText() {
        List<UIWidget> widgets = parser.generateWidgets(temporaryEntity, this);
        if (oldWidgets != null) {
            for(UIWidget w : oldWidgets) {
                variables.removeWidget(w);
            }
        }
        oldWidgets = widgets;
        for (UIWidget w : oldWidgets) {
            variables.addWidget(w);
        }
    }

    public void setVariable(String key, EntityRef value) {
        temporaryEntity.getComponent(ArgumentContainerComponent.class).arguments.put(key, value);
        temporaryEntity.saveComponent(temporaryEntity.getComponent(ArgumentContainerComponent.class));

        generateText();
    }
}
