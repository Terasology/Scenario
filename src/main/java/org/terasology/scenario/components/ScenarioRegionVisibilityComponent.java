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

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

import java.util.HashSet;
import java.util.Set;

/**
 * Component attached to a hubtool that includes a set of all of the region entities that should be visible to the
 * local player (visibility of the regions are ticked to true)
 */
@Replicate(FieldReplicateType.SERVER_TO_OWNER)
public class ScenarioRegionVisibilityComponent implements Component {
    @Replicate(FieldReplicateType.OWNER_TO_SERVER)
    public Set<EntityRef> visibleList = new HashSet<>();
}
