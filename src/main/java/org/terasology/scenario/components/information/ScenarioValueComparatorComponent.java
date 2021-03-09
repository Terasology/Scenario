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
package org.terasology.scenario.components.information;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.scenario.components.ScenarioArgumentContainerComponent;

/**
 * Value component for a Scenario argument entity, Contains a comparator value
 *
 * Argument Entities detailed in {@link ScenarioArgumentContainerComponent}
 */
@Replicate
public class ScenarioValueComparatorComponent implements Component {
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

        private String stringRepresentation;

        comparison(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        public String toString() {
            return stringRepresentation;
        }

        public abstract boolean evaluate(int x, int y);
    }

    public comparison compare = comparison.EQUAL_TO;
}
