// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.scenario.components.information;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, Contains a comparator value
 * <p>
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioValueComparatorComponent implements Component {
    public comparison compare = comparison.EQUAL_TO;

    public enum comparison {
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

        private final String stringRepresentation;

        comparison(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        public String toString() {
            return stringRepresentation;
        }

        public abstract boolean evaluate(int x, int y);
    }
}
