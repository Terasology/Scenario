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
package org.terasology.scenario.components;

import org.terasology.entitySystem.Component;

/**
 * Component that denotes an action.
 * Will eventually be abstracted like events are into their own components based on action type
 */
public class ActionComponent implements Component {
    public ActionType type;
    public short itemId = 10;
    public int numItems = 1;
    public String itemIdName;


    public enum ActionType {
        GIVE_ITEM
    }
}
