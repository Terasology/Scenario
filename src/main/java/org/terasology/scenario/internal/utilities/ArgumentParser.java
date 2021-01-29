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
package org.terasology.scenario.internal.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.nui.Color;
import org.terasology.nui.FontColor;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;
import org.terasology.scenario.components.ScenarioLogicTextComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateDisplayEvent;
import org.terasology.scenario.internal.ui.EditParameterScreen;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser designed to parse a {@link ScenarioLogicTextComponent} text of a scenario logic entity
 */
@Share(ArgumentParser.class)
@RegisterSystem(RegisterMode.CLIENT)
public class ArgumentParser extends BaseComponentSystem {
    private Logger logger = LoggerFactory.getLogger(ArgumentParser.class);

    @In
    private BlockManager blockManager;

    @In
    private EntityManager entityManager;

    @In
    private AssetManager assetManager;

    private List<String> keys;

    private Color specialColor = new Color(0, 191, 255);

    /**
     * Takes in an entity with a textComponent and argumentContainerComponent and parses the arguments from the text and
     * sets the default values in the argument container and saves the entity
     */
    public void parseDefaults(EntityRef entity) {
        String text = entity.getComponent(ScenarioLogicTextComponent.class).text;
        ScenarioArgumentContainerComponent args = entity.getComponent(ScenarioArgumentContainerComponent.class);
        if (entity.hasComponent(ScenarioArgumentContainerComponent.class)) { //Some cases might not have any arguments
            args.arguments = new HashMap<>();
        }

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon + 1);
            if (type.equals("Integer")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantInt",
                    Prefab.class).get()));
            } else if (type.equals("Block")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantBlock",
                    Prefab.class).get()));
            } else if (type.equals("Player")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantPlayer",
                    Prefab.class).get()));
            } else if (type.equals("String")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantString",
                    Prefab.class).get()));
            } else if (type.equals("Item")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario" +
                    ":scenarioConstantItemPrefab", Prefab.class).get()));
            } else if (type.equals("Comparator")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario" +
                    ":scenarioConstantComparator", Prefab.class).get()));
            } else if (type.equals("Region")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantRegion",
                    Prefab.class).get()));
            } else {
                //String parsed incorrectly, should throw some kind of exception probably
                return;
            }
        }

        if (args != null) {
            entity.saveComponent(args);
        }
    }

    /**
     * Takes in an entity with a textComponent and argumentContainerComponent and parses the text along with the
     * arguments in order to generate the text and coloring that should be used for display on a node in the UI
     */
    public String parseDisplayText(EntityRef entity) {
        String text = entity.getComponent(ScenarioLogicTextComponent.class).text;
        ScenarioArgumentContainerComponent args = entity.getComponent(ScenarioArgumentContainerComponent.class);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);

        List<String> replacements = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon + 1);
            EvaluateDisplayEvent event = new EvaluateDisplayEvent();
            args.arguments.get(key).send(event);
            replacements.add(event.getResult());
        }

        Pattern replacePattern = Pattern.compile("\\[.*?\\]");
        Matcher replaceMatcher = replacePattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while (replaceMatcher.find()) {
            replaceMatcher.appendReplacement(sb, FontColor.getColored(replacements.get(index), specialColor));
            index++;
        }
        replaceMatcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * Takes in an entity with a textComponent and argumentContainerComponent and parses the arguments from the text and
     * generates a list of widgets to allow for editing the parameters or just text on elements that do not have a
     * parameter
     */
    public List<UIWidget> generateWidgets(EntityRef entity, CoreScreenLayer editScreen) {
        List<UIWidget> output = new ArrayList<>();
        String text = entity.getComponent(ScenarioLogicTextComponent.class).text;
        ScenarioArgumentContainerComponent args = entity.getComponent(ScenarioArgumentContainerComponent.class);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        List<String> replacements = new ArrayList<>();
        keys = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon + 1);
            EvaluateDisplayEvent event = new EvaluateDisplayEvent();
            args.arguments.get(key).send(event);
            replacements.add(event.getResult());
            keys.add(key);
        }

        Pattern replacePattern = Pattern.compile("\\[.*?\\]");
        Matcher replaceMatcher = replacePattern.matcher(text);
        int index = 0;
        int end = 0;
        int start = 0;
        while (replaceMatcher.find()) {
            start = replaceMatcher.start();
            UILabel label = new UILabel();
            label.setText(text.substring(end, start));
            output.add(label);

            end = replaceMatcher.end();
            UIButton button = new UIButton();
            button.setText(FontColor.getColored(replacements.get(index), specialColor));
            String tempKey = keys.get(index);
            EntityRef tempEntity = args.arguments.get(tempKey);
            button.subscribe(b -> {
                EditParameterScreen screen = editScreen.getManager().pushScreen(EditParameterScreen.ASSET_URI,
                    EditParameterScreen.class);
                screen.setupParameter(tempKey, tempEntity, editScreen, this);
            });
            output.add(button);
            index++;

        }
        UILabel label = new UILabel();
        label.setText(text.substring(end));
        output.add(label);
        return output;
    }
}
