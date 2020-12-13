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
import org.joml.primitives.Rectanglei;
import org.joml.Vector2i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.nui.Canvas;
import org.terasology.nui.FontColor;
import org.terasology.nui.TextLineBuilder;
import org.terasology.nui.UIWidget;
import org.terasology.nui.WidgetUtil;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.itemRendering.AbstractItemRenderer;
import org.terasology.nui.layouts.ColumnLayout;
import org.terasology.nui.util.RectUtility;
import org.terasology.nui.widgets.UIDropdownScrollable;
import org.terasology.nui.widgets.UILabel;
import org.terasology.nui.widgets.UIText;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.scenario.components.ScenarioComponent;
import org.terasology.scenario.components.ScenarioLogicLabelComponent;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.information.ScenarioValueBlockUriComponent;
import org.terasology.scenario.components.information.ScenarioValueComparatorComponent;
import org.terasology.scenario.components.information.ScenarioValueIntegerComponent;
import org.terasology.scenario.components.information.ScenarioValueItemPrefabUriComponent;
import org.terasology.scenario.components.information.ScenarioValueRegionComponent;
import org.terasology.scenario.components.information.ScenarioValueStringComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeBlockComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeComparatorComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeIntegerComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeItemComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeRegionComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeStringComponent;
import org.terasology.scenario.components.regions.RegionColorComponent;
import org.terasology.scenario.components.regions.RegionNameComponent;
import org.terasology.scenario.internal.utilities.ArgumentParser;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Screen for editing a parameter/argument of a logic entity, this screen is designed to be recursive in case parameters
 * can have multiple values, such as a random int being an integer parameter, but itself also needs two integer parameters. Does not
 * need to be sent to the server like the logic screen because this is still client side editing and will return to a logic screen before
 * finishing an edit, which that screen would be the one to send to the server
 */
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
    private UIDropdownScrollable<String> itemDropdown;
    private UIDropdownScrollable<EntityRef> regionDropdown;
    private UIDropdownScrollable<ScenarioValueComparatorComponent.comparison> comparisonDropdown;
    private List<UIWidget> oldWidgets;

    private List<String> blocksURI;
    private List<String> itemPrefabs;


    private ArgumentParser parser;


    @Override
    public void initialise() {
        variables = find("variableWidget", ColumnLayout.class);
        editLabel = find("editLabel", UILabel.class);
        selectionLabel = find("selectLabel", UILabel.class);
        dropdown = find("selectionDropdown", UIDropdownScrollable.class);

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

        itemPrefabs = new ArrayList<>();

        Iterable<Prefab> tempPrefabs = prefabManager.listPrefabs(ItemComponent.class);
        for (Prefab p : tempPrefabs) {
            itemPrefabs.add(p.getUrn().toString());
        }
    }

    public void setupParameter(String k, EntityRef entity, CoreScreenLayer coreScreenLayer, ArgumentParser argumentParser) {
        this.key = k;
        this.baseEntity = entity;
        this.tempEntity = baseEntity.copy();
        this.returnScreen = coreScreenLayer;
        this.parser = argumentParser;

        optionList = new ArrayList<>();
        if (entity.hasComponent(ScenarioTypeIntegerComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioTypeIntegerComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Integer");
            selectionLabel.setText("Select an Integer type");
        } else if (entity.hasComponent(ScenarioTypeStringComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioTypeStringComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit String");
            selectionLabel.setText("Select a String type");
        } else if (entity.hasComponent(ScenarioTypeBlockComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioTypeBlockComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Block");
            selectionLabel.setText("Select a Block type");
        } else if (entity.hasComponent(ScenarioTypeItemComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioTypeItemComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Item");
            selectionLabel.setText("Select a Item type");
        } else if (entity.hasComponent(ScenarioTypeComparatorComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioTypeComparatorComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Comparator");
            selectionLabel.setText("Select a Comparator type");
        } else if (entity.hasComponent(ScenarioTypeRegionComponent.class)) {
            Iterable<Prefab> prefabs = prefabManager.listPrefabs(ScenarioTypeRegionComponent.class);
            for (Prefab p : prefabs) {
                optionList.add(p);
            }
            editLabel.setText("Edit Region");
            selectionLabel.setText("Select a region");
        }

        dropdown.setOptions(optionList);
        setPrefabStart(entity);
        dropdown.setSelection(entity.getParentPrefab());
        dropdown.setOptionRenderer(new AbstractItemRenderer<Prefab>() {
            @Override
            public void draw(Prefab value, Canvas canvas) {
                Rectanglei textRegion = RectUtility.createFromMinAndSize(0, 0, canvas.getRegion().lengthX(), canvas.getRegion().lengthY());
                canvas.drawText(value.getComponent(ScenarioLogicLabelComponent.class).name, textRegion);
            }

            @Override
            public Vector2i getPreferredSize(Prefab value, Canvas canvas) {
                Font font = canvas.getCurrentStyle().getFont();
                List<String> lines = TextLineBuilder.getLines(font, value.getComponent(ScenarioLogicLabelComponent.class).name, canvas.size().x);
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
            tempEntity.getComponent(ScenarioValueIntegerComponent.class).value = Integer.parseInt(textEntry.getText());
            tempEntity.saveComponent(tempEntity.getComponent(ScenarioValueIntegerComponent.class));
        } else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantString"))) {
            tempEntity.getComponent(ScenarioValueStringComponent.class).string = textEntry.getText();
            tempEntity.saveComponent(tempEntity.getComponent(ScenarioValueStringComponent.class));
        } else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantBlock"))) {
            tempEntity.getComponent(ScenarioValueBlockUriComponent.class).block_uri = blockDropdown.getSelection();
            tempEntity.saveComponent(tempEntity.getComponent(ScenarioValueBlockUriComponent.class));
        } else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantItemPrefab"))) {
            tempEntity.getComponent(ScenarioValueItemPrefabUriComponent.class).prefabURI = itemDropdown.getSelection();
            tempEntity.saveComponent(tempEntity.getComponent(ScenarioValueItemPrefabUriComponent.class));
        } else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantComparator"))) {
            tempEntity.getComponent(ScenarioValueComparatorComponent.class).compare = comparisonDropdown.getSelection();
            tempEntity.saveComponent(tempEntity.getComponent(ScenarioValueComparatorComponent.class));
        } else if (tempEntity.getParentPrefab().equals(prefabManager.getPrefab("scenario:scenarioConstantRegion"))) {
            tempEntity.getComponent(ScenarioValueRegionComponent.class).regionEntity = regionDropdown.getSelection();
            tempEntity.saveComponent(tempEntity.getComponent(ScenarioValueRegionComponent.class));
        }
        if (!tempEntity.equals(baseEntity)) {
            if (returnScreen instanceof EditLogicScreen) {
                ((EditLogicScreen) returnScreen).setVariable(key, tempEntity);
            } else { //Must be a parameter(recursive) screen
                ((EditParameterScreen) returnScreen).setVariable(key, tempEntity);
            }
        } else {
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
        if (tempEntity.hasComponent(ScenarioValueIntegerComponent.class) || //Check for constant/base cases
            tempEntity.hasComponent(ScenarioValueStringComponent.class)) {
            String entryValue;
            UIText entry = new UIText();
            entry.setReadOnly(false);
            if (tempEntity.hasComponent(ScenarioValueIntegerComponent.class)) {
                entryValue = Integer.toString(tempEntity.getComponent(ScenarioValueIntegerComponent.class).value);
            } else {
                entryValue = tempEntity.getComponent(ScenarioValueStringComponent.class).string;
            }
            entry.setText(entryValue);
            emptyVariables();

            textEntry = entry;
            variables.addWidget(entry);
        } else if (tempEntity.hasComponent(ScenarioValueBlockUriComponent.class)) {
            emptyVariables();

            blockDropdown = new UIDropdownScrollable<>();
            blockDropdown.setOptions(blocksURI);
            blockDropdown.setSelection(tempEntity.getComponent(ScenarioValueBlockUriComponent.class).block_uri);

            variables.addWidget(blockDropdown);
        } else if (tempEntity.hasComponent(ScenarioValueItemPrefabUriComponent.class)) {
            emptyVariables();

            itemDropdown = new UIDropdownScrollable<>();
            itemDropdown.setOptions(itemPrefabs);
            itemDropdown.setSelection(tempEntity.getComponent(ScenarioValueItemPrefabUriComponent.class).prefabURI);

            variables.addWidget(itemDropdown);
        } else if (tempEntity.hasComponent(ScenarioValueComparatorComponent.class)) {
            emptyVariables();

            comparisonDropdown = new UIDropdownScrollable<>();
            comparisonDropdown.setOptions(Arrays.asList(ScenarioValueComparatorComponent.comparison.values()));
            comparisonDropdown.setSelection(tempEntity.getComponent(ScenarioValueComparatorComponent.class).compare);

            variables.addWidget(comparisonDropdown);
        } else if (tempEntity.hasComponent(ScenarioValueRegionComponent.class)) {
            emptyVariables();

            regionDropdown = new UIDropdownScrollable<>();
            if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
                EntityRef scenario = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
                if (scenario == null) {
                    return;
                }
                List<EntityRef> regions = scenario.getComponent(ScenarioComponent.class).regionEntities;
                regionDropdown.setOptions(regions);
                regionDropdown.setOptionRenderer(new AbstractItemRenderer<EntityRef>() {
                    @Override
                    public void draw(EntityRef value, Canvas canvas) {
                        String text = FontColor.getColored(value.getComponent(RegionNameComponent.class).regionName,
                            value.getComponent(RegionColorComponent.class).color);
                        canvas.drawText(text);
                    }

                    @Override
                    public Vector2i getPreferredSize(EntityRef value, Canvas canvas) {
                        Font font = canvas.getCurrentStyle().getFont();
                        String text = value.getComponent(RegionNameComponent.class).regionName;
                        List<String> lines = TextLineBuilder.getLines(font, text, canvas.size().x);
                        return font.getSize(lines);
                    }
                });

                variables.addWidget(regionDropdown);
            }
        } else {
            emptyVariables();
            oldWidgets = parser.generateWidgets(tempEntity, this);
            for (UIWidget u : oldWidgets) {
                variables.addWidget(u);
            }
        }
    }

    private void setVariable(String k, EntityRef value) {
        tempEntity.getComponent(ScenarioArgumentContainerComponent.class).arguments.put(k, value);
        tempEntity.saveComponent(tempEntity.getComponent(ScenarioArgumentContainerComponent.class));

        setupInteraction();
    }

    private void emptyVariables() {
        if (textEntry != null) {
            variables.removeWidget(textEntry);
        }
        if (oldWidgets != null) {
            for (UIWidget w : oldWidgets) {
                variables.removeWidget(w);
            }
        }
        if (blockDropdown != null) {
            variables.removeWidget(blockDropdown);
        }
        if (itemDropdown != null) {
            variables.removeWidget(itemDropdown);
        }
        if (comparisonDropdown != null) {
            variables.removeWidget(comparisonDropdown);
        }
        if (regionDropdown != null) {
            variables.removeWidget(regionDropdown);
        }
    }
}
