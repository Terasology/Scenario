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
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.In;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.scenario.components.actions.ArgumentContainerComponent;
import org.terasology.scenario.components.actions.TextComponent;
import org.terasology.scenario.components.information.BlockComponent;
import org.terasology.scenario.components.information.ConstIntegerComponent;
import org.terasology.scenario.components.information.InformationEnums;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateBlockDisplayEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluateIntDisplayEvent;
import org.terasology.scenario.internal.events.evaluationEvents.EvaluatePlayerDisplayEvent;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {
    private Logger logger = LoggerFactory.getLogger(ArgumentParser.class);


    private BlockManager blockManager;


    private EntityManager entityManager;

    private static ArgumentParser parser;

    private ArgumentParser() {

    }

    public static ArgumentParser getInstance() {
        if (parser == null) {
            parser = new ArgumentParser();
        }
        return parser;
    }

    public void setBlockManager(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Takes in an entity with a textComponent and argumentContainerComponent and parses the arguments from the text
     * and sets the default values in the argument container and saves the entity
     */
    public void parseDefaults (EntityRef entity) {
        String text = entity.getComponent(TextComponent.class).text;
        ArgumentContainerComponent args = entity.getComponent(ArgumentContainerComponent.class);
        args.arguments = new HashMap<>();

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            String group = matcher.group(1);
            int indexColon = group.indexOf(":");
            String key = group.substring(0, indexColon);
            String type = group.substring(indexColon+1);
            Component defaultComponent;
            if (type.equals("Integer")) {
                defaultComponent = new ConstIntegerComponent();
                ((ConstIntegerComponent)defaultComponent).value = 1;
            }
            else if (type.equals("Block")) {
                defaultComponent = new BlockComponent();
                ((BlockComponent)defaultComponent).value = blockManager.getBlockFamily("core:stone");
            }
            else if (type.equals("Player")) {
                defaultComponent = new PlayerComponent();
                ((PlayerComponent)defaultComponent).type = InformationEnums.PlayerType.TRIGGERING_PLAYER;
            }
            else {
                //String parsed incorrectly, should throw some kind of exception probably
                return;
            }
            args.arguments.put(key, entityManager.create(defaultComponent));
        }

        entity.saveComponent(args);
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
            if (type.equals("Integer")) {
                EvaluateIntDisplayEvent event = new EvaluateIntDisplayEvent();
                args.arguments.get(key).send(event);
                replacements.add(event.getResult());
            }
            else if (type.equals("Block")) {
                EvaluateBlockDisplayEvent event = new EvaluateBlockDisplayEvent();
                args.arguments.get(key).send(event);
                replacements.add(event.getResult());
            }
            else if (type.equals("Player")) {
                EvaluatePlayerDisplayEvent event = new EvaluatePlayerDisplayEvent();
                args.arguments.get(key).send(event);
                replacements.add(event.getResult());
            }
            else {
                //String parsed incorrectly, should throw some kind of exception probably
                return "";
            }
        }

        Pattern replacePattern = Pattern.compile("\\[.*?\\]");
        Matcher replaceMatcher = replacePattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while(replaceMatcher.find()) {
            replaceMatcher.appendReplacement(sb, FontColor.getColored(replacements.get(index), Color.BLUE));
            index++;
        }
        return sb.toString();
    }
}
