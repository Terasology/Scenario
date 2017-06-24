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
package org.terasology.scenario.components.information;

/**
 * This is not actually a component, it details all enums for any action/condition/event components along with the editing screens
 * that will be used between different types
 */
public final class InformationEnums {
    /**
     * Any types that are used as components, also includes any managers that might need to be passed at some point for
     * and action to do it's action
     */
    public enum DataTypes {
        PLAYER,
        INTEGER,
        BLOCK,
        ENTITY_MANAGER,
        BLOCK_MANAGER
    }
    public enum PlayerType {
        TRIGGERING_PLAYER,
        TARGETED_PLAYER,
    }
}
