// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.information.IndentificationComponents.ScenarioTypeIntegerComponent;
import org.terasology.scenario.components.information.ScenarioValueStringComponent;

import java.util.Map;

/**
 * Argument Container component for a Scenario logic entity, NEEDS to be includes if the description includes argument parameters
 *
 * Argument entities include:
 *   Network Component - This is just the default network component for a terasology entity {@link NetworkComponent}
 *   Type Component - This is a component used to denote the type of variable that the entity is representing, example is {@link ScenarioTypeIntegerComponent}
 *   Value or Expression Component (Values are constant values, expressions are evaluated to obtain the value)
 *        - This is the component that holds the actual value or expression representation for the entity, example is {@link ScenarioValueStringComponent}
 *
 * Scenario logic entities are detailed in {@link ScenarioComponent}
 */
@Replicate
public class ScenarioArgumentContainerComponent implements Component<ScenarioArgumentContainerComponent> {
    @Replicate
    public Map<String, EntityRef> arguments;
}
