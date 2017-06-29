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
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.actions.TextComponent;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioPlayerEntityComponent;
import org.terasology.scenario.components.information.InformationEnums;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateDisplayEvent;
import org.terasology.scenario.internal.ui.EditParameterScreen;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {
    private Logger logger = LoggerFactory.getLogger(ArgumentParser.class);


    private BlockManager blockManager;


    private EntityManager entityManager;

    private AssetManager assetManager;

    private static ArgumentParser parser;

    private List<String> keys;

    public void setBlockManager(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Takes in an entity with a textComponent and argumentContainerComponent and parses the arguments from the text
     * and sets the default values in the argument container and saves the entity
     */
    public void parseDefaults (EntityRef entity) {
        String text = entity.getComponent(TextComponent.class).text;
        ArgumentContainerComponent args = entity.getComponent(ArgumentContainerComponent.class);
        if (entity.hasComponent(ArgumentContainerComponent.class)) { //Some cases might not have any arguments
            args.arguments = new HashMap<>();
        }

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon+1);
            Component defaultComponent;
            Component indicatorComponent;
            if (type.equals("Integer")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantInt", Prefab.class).get()));
            }
            else if (type.equals("Block")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantBlock", Prefab.class).get()));
            }
            else if (type.equals("Player")) {
                defaultComponent = new PlayerComponent();
                ((PlayerComponent)defaultComponent).type = InformationEnums.PlayerType.TRIGGERING_PLAYER;
                indicatorComponent = new ScenarioPlayerEntityComponent();
                args.arguments.put(key, entityManager.create(defaultComponent, indicatorComponent));
            }
            else if (type.equals("String")) {
                args.arguments.put(key, entityManager.create(assetManager.getAsset("scenario:scenarioConstantString", Prefab.class).get()));
            }
            else {
                //String parsed incorrectly, should throw some kind of exception probably
                return;
            }

        }

        if (args != null) {
            entity.saveComponent(args);
        }
    }

    /**
     * Takes in an entity with a textComponent and argumentContainerComponent and parses the text along with the arguments
     * in order to generate the text and coloring that should be used for display on a node in the UI
     */
    public String parseDisplayText (EntityRef entity) {
        String text = entity.getComponent(TextComponent.class).text;
        ArgumentContainerComponent args = entity.getComponent(ArgumentContainerComponent.class);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);

        List<String> replacements = new ArrayList<>();
        while(matcher.find()){
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon+1);
            EvaluateDisplayEvent event = new EvaluateDisplayEvent();
            args.arguments.get(key).send(event);
            replacements.add(event.getResult());
        }

        Pattern replacePattern = Pattern.compile("\\[.*?\\]");
        Matcher replaceMatcher = replacePattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while(replaceMatcher.find()) {
            replaceMatcher.appendReplacement(sb, FontColor.getColored(replacements.get(index), Color.BLUE));
            index++;
        }
        replaceMatcher.appendTail(sb);
        return sb.toString();
    }

    public List<UIWidget> generateWidgets(EntityRef entity, CoreScreenLayer editScreen) {
        List<UIWidget> output = new ArrayList<>();
        String text = entity.getComponent(TextComponent.class).text;
        ArgumentContainerComponent args = entity.getComponent(ArgumentContainerComponent.class);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        List<String> replacements = new ArrayList<>();
        keys = new ArrayList<>();
        while(matcher.find()){
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon+1);
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
        while(replaceMatcher.find()) {
            start = replaceMatcher.start();
            UILabel label = new UILabel();
            label.setText(text.substring(end, start));
            output.add(label);

            end = replaceMatcher.end();
            UIButton button = new UIButton();
            button.setText(FontColor.getColored(replacements.get(index), Color.BLUE));
            String tempKey = keys.get(index);
            EntityRef tempEntity = args.arguments.get(tempKey);
            button.subscribe(b -> {
                EditParameterScreen screen = editScreen.getManager().pushScreen(EditParameterScreen.ASSET_URI, EditParameterScreen.class);
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
