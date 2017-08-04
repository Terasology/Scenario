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
package org.terasology.scenario.components.regions;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Vector3i;
import org.terasology.network.FieldReplicateType;
import org.terasology.network.Replicate;
import org.terasology.scenario.internal.systems.RegionSystem;
import org.terasology.math.Region3i;
import org.terasology.structureTemplates.components.ProtectedRegionsComponent;

/**
 * Creation component for a Scenario argument entity, indicates that this region is currently being created
 *
 * Region entities include:
 *   Network Component
 *   RegionName Component - field indicates the name of the region
 *   RegionColor Component - field indicates the color of the region
 *   RegionContainingEntities Component - field contains a list that is monitored by {@link RegionSystem} of what player entities are within the region
 *   RegionLocation Component - field is the actual region in the world as a {@link Region3i}
 *   * RegionBeingCreated Component
 *   * {@link ProtectedRegionsComponent}
 *
 *   * indicates optional (RegionBeingCreated meaning it is currently being created, ProtectedRegion meaning the region
 *     is being protected by the structureTemplates system and will prevent alterations being made to the land within the region
 */
public class RegionBeingCreatedComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Vector3i firstHit;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public EntityRef creatingEntity;
}
