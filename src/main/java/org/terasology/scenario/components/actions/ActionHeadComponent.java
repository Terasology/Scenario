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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

public class ActionHeadComponent implements Component {
    /**
     * The Entity that actually attributes what type of action this component is, allows all of the actions
     * to be treated the same, grab the headComponent and reference what component is attached through this variable with
     * out needing to do a search for which component is actually attached.
     *
     * This EntityRef MUST only have one component of which satisfies the interface of ActionComponent
     */
    public EntityRef action;
}
