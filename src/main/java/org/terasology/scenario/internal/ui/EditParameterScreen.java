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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
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
import org.terasology.rendering.nui.widgets.UIDropdown;
import org.terasology.rendering.nui.widgets.UIDropdownScrollable;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.scenario.components.ShortNameComponent;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.information.ConstBlockComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.ConstStringComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioBlockComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioIntegerComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioStringComponent;
import org.terasology.scenario.internal.utilities.ArgumentParser;
import org.terasology.world.block.BlockExplorer;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.shapes.BlockShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EditParameterScreen extends CoreScreenLayer {
    public static final ResourceUrn ASSET_URI = new ResourceUrn("scenario:editParameterScreen!instance");

    private static final Logger logger = LoggerFactory.getLogger(EditParameterScreen.class);

    @In
    PrefabManager prefabManager;

    @In
    EntityManager entityManager;

    @In
    AssetManager assetManager;

    @In
    BlockManager blockManager;

    private CoreScreenLayer returnScreen;
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
    private UIDropdownScrollable<String> blockDropdown;
    private List<UIWidget> oldWidgets;

    private List<String> blocksURI;

    private ArgumentParser parser;


    @Override
    public void initialise() {
        variables = find("variableWidget", ColumnLayout.class);
        editLabel = find("editLabel", UILabel.class);
        selectionLabel = find("selectLabel", UILabel.class);
        dropdown = find ("selectionDropdown", UIDropdownScrollable.class);

        WidgetUtil.trySubscribe(this, "okButton", this::onOkButton);
        WidgetUtil.trySubscribe(this, "cancelButton", this::onCancelButton);

        Set<BlockUri> blocks = Sets.newHashSet();
        Iterables.addAll(blocks, blockManager.listRegisteredBlockUris());

        List<BlockUri> blockList = Lists.newArrayList(blocks);
        blockList.sort((BlockUri o1, BlockUri o2) -> o1.toString().compareTo(o2.toString()));

        blocksURI = new ArrayList<>();
        for (BlockUri block : blockList) {
            if (!block.equals(BlockManager.AIR_ID) && !block.equals(BlockManager.UNLOADED_ID)) {
                blocksURI.add(block.toString());
            }
        }
    }

    public void setupParameter(String key, EntityRef entity, CoreScreenLayer returnScreen, ArgumentParser parser) {
        this.key = key;
        this.baseEntity = entity;
        this.tempEntity = baseEntity.copy();
        this.returnScreen = returnScreen;
        this.parser = parser;

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
        else if (entity.hasComponent(ScenarioBlockComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioBlockComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Block");
            selectionLabel.setText("Select a Block type");
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
        if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantInt"))) {
            tempEntity.getComponent(ConstIntegerComponent.class).value = Integer.parseInt(textEntry.getText());
            tempEntity.saveComponent(tempEntity.getComponent(ConstIntegerComponent.class));
        }
        else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantString"))) {
            tempEntity.getComponent(ConstStringComponent.class).string = textEntry.getText();
            tempEntity.saveComponent(tempEntity.getComponent(ConstStringComponent.class));
        }
        else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantBlock"))) {
            tempEntity.getComponent(ConstBlockComponent.class).block_uri = blockDropdown.getSelection();
            tempEntity.saveComponent(tempEntity.getComponent(ConstBlockComponent.class));
        }
        if (!tempEntity.equals(baseEntity)) {
            if (returnScreen instanceof EditLogicScreen) {
                ((EditLogicScreen)returnScreen).setVariable(key, tempEntity);
            }
            else { //Must be a parameter(recursive) screen
                ((EditParameterScreen)returnScreen).setVariable(key, tempEntity);
            }

        }
        else {
            if (tempEntity.exists()) {
                tempEntity.destroy();
            }
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
        parser.parseDefaults(tempEntity);

        setupInteraction();
    }

    private void setPrefabStart(EntityRef entity) {
        selectedPrefab = entity.getParentPrefab();
        tempEntity = entity.copy();
        setupInteraction();
    }

    private void setupInteraction() {
        if (tempEntity.hasComponent(ConstIntegerComponent.class) || //Check for constant/base cases
                tempEntity.hasComponent(ConstStringComponent.class)) {
            String entryValue;
            UIText entry = new UIText();
            entry.setReadOnly(false);
            entryValue = Integer.toString(tempEntity.getComponent(ConstIntegerComponent.class).value);
            entry.setText(entryValue);
            if (textEntry != null) {
                variables.removeWidget(textEntry);
            }
            if (oldWidgets != null) {
                for(UIWidget w : oldWidgets) {
                    variables.removeWidget(w);
                }
            }
            if (blockDropdown != null) {
                variables.removeWidget(blockDropdown);
            }

            textEntry = entry;
            variables.addWidget(entry);
        }
        else if(tempEntity.hasComponent(ConstBlockComponent.class)) {
            if (textEntry != null) {
                variables.removeWidget(textEntry);
            }
            if (oldWidgets != null) {
                for(UIWidget w : oldWidgets) {
                    variables.removeWidget(w);
                }
            }
            if (blockDropdown != null) {
                variables.removeWidget(blockDropdown);
            }

            blockDropdown = new UIDropdownScrollable<>();
            blockDropdown.setOptions(blocksURI);
            blockDropdown.setSelection(tempEntity.getComponent(ConstBlockComponent.class).block_uri);

            variables.addWidget(blockDropdown);
        }
        else {
            if (textEntry != null) {
                variables.removeWidget(textEntry);
            }
            if (oldWidgets != null) {
                for(UIWidget w : oldWidgets) {
                    variables.removeWidget(w);
                }
            }
            if (blockDropdown != null) {
                variables.removeWidget(blockDropdown);
            }

            oldWidgets = parser.generateWidgets(tempEntity, this);
            for (UIWidget u : oldWidgets) {
                variables.addWidget(u);
            }
        }
    }

    private void setVariable(String key, EntityRef value) {
        tempEntity.getComponent(ArgumentContainerComponent.class).arguments.put(key, value);
        tempEntity.saveComponent(tempEntity.getComponent(ArgumentContainerComponent.class));

        setupInteraction();
    }

}
