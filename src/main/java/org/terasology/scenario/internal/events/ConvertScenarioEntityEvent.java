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
package org.terasology.scenario.internal.events;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.ConvertEntitySystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Event called to convert a logic entity into a list of serialised strings using ConvertEntitySystem that will allow for
 * entity recreation using {@link ConvertEntitySystem}
 */
public class ConvertScenarioEntityEvent implements Event {
    private List<String> outputList = new ArrayList<>();
    private String prefix;

    public ConvertScenarioEntityEvent() {
        prefix = "";
    }

    public ConvertScenarioEntityEvent(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<String> outputList) {
        this.outputList = outputList;
    }

    public String getPrefix() {
        return prefix;
    }
}
