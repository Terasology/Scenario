// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.internal.events;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.scenario.internal.systems.ConvertEntitySystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Event called to convert a logic entity into a list of serialised strings using ConvertEntitySystem that will allow for entity recreation
 * using {@link ConvertEntitySystem}
 */
public class ConvertScenarioEntityEvent implements Event {
    private List<String> outputList = new ArrayList<>();
    private final String prefix;

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
