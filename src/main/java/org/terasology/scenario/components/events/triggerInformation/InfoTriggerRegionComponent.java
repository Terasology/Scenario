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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
/**
 * Component that is attached to the data entity that is passed when an event in the Scenario logic occurs.
 *
 * This component includes information about the triggered region;
 *   The entity of the region that was triggered (example: the joined region or the region that was just exited)
 */
public class InfoTriggerRegionComponent implements Component {
    public EntityRef region;
}
