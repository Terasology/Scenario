// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, Contains a comparator value
 *
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioValueComparatorComponent implements Component<ScenarioValueComparatorComponent> {
    public enum Comparison {
        GREATER_THAN(">") {
            public boolean evaluate(int x, int y) {
                return x > y;
            }
        },
        LESS_THAN("<") {
            public boolean evaluate(int x, int y) {
                return x < y;
            }
        },
        EQUAL_TO("=") {
            public boolean evaluate(int x, int y) {
                return x == y;
            }
        },
        NOT_EQUAL_TO("!=") {
            public boolean evaluate(int x, int y) {
                return x != y;
            }
        },
        GREATER_THAN_EQUAL_TO(">=") {
            public boolean evaluate(int x, int y) {
                return x >= y;
            }
        },
        LESS_THAN_EQUAL_TO("<=") {
            public boolean evaluate(int x, int y) {
                return x <= y;
            }
        };

        private String stringRepresentation;

        Comparison(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        public String toString() {
            return stringRepresentation;
        }

        public abstract boolean evaluate(int x, int y);
    }

    public Comparison compare = Comparison.EQUAL_TO;

    @Override
    public void copy(ScenarioValueComparatorComponent other) {
        this.compare = other.compare;
    }
}
