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
package org.terasology.scenario.components.actions;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.events.GiveItemEvent;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.scenario.components.information.BlockComponent;
import org.terasology.scenario.components.information.InformationEnums;
import org.terasology.scenario.components.information.IntegerComponent;
import org.terasology.scenario.components.information.PlayerComponent;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.HashMap;
import java.util.Map;

public class GiveBlockActionComponent implements ActionComponent {
    public InformationEnums.PlayerType player;
    public int amount;
    public BlockFamily block;

    public Map<String, EntityRef> variables;
    public Map<String, InformationEnums.DataTypes> neededTypes;
    public Map<String, InformationEnums.DataTypes> types;

    public GiveBlockActionComponent(){
    }

    /**
     * Create a new give block component
     * @param block Starting blockFamily(usually will be stone upon construction)
     * @param entityManager Needs a reference to the entityManager for usage to construct entities
     */
    public GiveBlockActionComponent(BlockFamily block, EntityManager entityManager) {
        this.player = InformationEnums.PlayerType.TRIGGERING_PLAYER;
        this.amount = 1;
        this.block = block;

        PlayerComponent playerComp = new PlayerComponent();
        playerComp.type = InformationEnums.PlayerType.TRIGGERING_PLAYER;
        IntegerComponent amountComp = new IntegerComponent();
        amountComp.value = this.amount;
        BlockComponent blockComp = new BlockComponent();
        blockComp.value = this.block;

        variables = new HashMap<>();
        variables.put("player", entityManager.create(playerComp));
        variables.put("amount", entityManager.create(amountComp));
        variables.put("block", entityManager.create(blockComp));

        types = new HashMap<>();
        types.put("player", InformationEnums.DataTypes.PLAYER);
        types.put("amount", InformationEnums.DataTypes.INTEGER);
        types.put("block", InformationEnums.DataTypes.BLOCK);

        neededTypes = new HashMap<>();
        neededTypes.put("player", InformationEnums.DataTypes.PLAYER);
    }


    @Override
    public String getParsableText() {
        return "Give [player:Player] [amount:Integer] of [block:Block]";
    }

    @Override
    public String getDisplayText() {
        return  "Give " + FontColor.getColored(player.name(), Color.BLUE) + " " +
                FontColor.getColored(Integer.toString(amount), Color.BLUE) +
                FontColor.getColored(block.getDisplayName(), Color.BLUE);
    }

    @Override
    public Map<String, EntityRef> getMapping(EntityManager entityManager) {
        PlayerComponent playerComp = new PlayerComponent();
        playerComp.type = InformationEnums.PlayerType.TRIGGERING_PLAYER;
        IntegerComponent amountComp = new IntegerComponent();
        amountComp.value = this.amount;
        BlockComponent blockComp = new BlockComponent();
        blockComp.value = this.block;

        variables = new HashMap<>();
        variables.put("player", entityManager.create(playerComp));
        variables.put("amount", entityManager.create(amountComp));
        variables.put("block", entityManager.create(blockComp));
        return variables;
    }

    @Override
    public Map<String, InformationEnums.DataTypes> getTypes() {
        return types;
    }

    @Override
    public Map<String, InformationEnums.DataTypes> neededTypes() {
        return neededTypes;
    }

    @Override
    public void triggerAction(Map<String, EntityRef> passedVariables, EntityManager entityManager) {
        BlockItemFactory blockItemFactory = new BlockItemFactory(entityManager);
        EntityRef item = blockItemFactory.newInstance(block, amount);
        if(!item.exists()) {
            throw new IllegalArgumentException("Unknown block or item");
        }
        PlayerComponent playerComp = passedVariables.get("player").getComponent(PlayerComponent.class);
        EntityRef player = playerComp.value;
        GiveItemEvent giveItemEvent = new GiveItemEvent(player);
        item.send(giveItemEvent);
        if (!giveItemEvent.isHandled()) {
            item.destroy();
        }

        for(Map.Entry<String, EntityRef> e : passedVariables.entrySet()) {
            e.getValue().destroy();
        }
    }

    @Override
    public void setVariable(ActionVariable variable) {
        if (variable.variable.equals("player")) {
            player = variable.value.getComponent(PlayerComponent.class).type;
            variables.get("player").destroy();
            variables.put("player", variable.value);
        }
        else if (variable.variable.equals("amount")) {
            amount = variable.value.getComponent(IntegerComponent.class).value;
            variables.get("amount").destroy();
            variables.put("amount", variable.value);
        }
        else if (variable.variable.equals("block")) {
            block = variable.value.getComponent(BlockComponent.class).value;
            variables.get("block").destroy();
            variables.put("block", variable.value);
        }
    }

    @Override
    public EntityRef getVariable(String variableName) {
        return variables.get(variableName);
    }
}
