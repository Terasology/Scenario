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
package org.terasology.scenario.components.events.triggerInformation;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;

/**
 * Component for a block being destroyed and the information passed with it
 */
public class DestroyedBlockComponent implements Component {
    /**
     * entity of the block that was destroyed
     */
    public EntityRef destroyedBlock;

    /**
     * entity of the character that destroyed the block
     */
    public EntityRef directCause;

    /**
     * prefab of the item used to destroy the block
     */
    public Prefab damageType;
}
