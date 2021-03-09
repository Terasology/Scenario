## Creation of a new expression for the Scenario Module

In this tutorial we will be creating a new value for the Scenario Module of Terasology.

In this example we will be creating a value that will calculate the number of a certain type of block that the player has in their inventory.

### Indicator Component

To start off we need to create a new Component that will be used to identify the expression. For this example we will be naming our new component `ScenarioExpressionBlockCountComponent.java`. 

Now we need to write our component class, for an indicator we need to do two steps, first we need to annotate the class with a `@Replicate` annotation and have it implement the `Component` interface. This results in our class looking like this:

```java
    import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

@Replicate
public class ScenarioExpressionBlockCountComponent implements Component {
}
```

### Prefab File

Now that we have our indicator component complete we need to create the `Prefab` file for the Expression. 

This file needs to be placed inside the module's `<module-name>/assets/prefabs` directory. In this case we will name the file `scenarioBlockCountExpression.prefab`

A scenario expression prefab needs to have a few components:
1) A network component
2) A type component
3) A ScenarioLogicLabel component
4) The indicator component
5) A ScenarioLogicText component
6) A ScenarioArgumentContainer(If the ScenarioLogicText text has any parameters, detailed below)

##### 1. Network Component
This is always required for every expression within the Scenario module, it allows the expression to be used on multiplayer.
    
##### 2. A Type Component
This is a component that indicates the type-ing of the expression and where the expression can be used, examples if this include `ScenarioTypeInteger` and `ScenarioTypeString`, a more complete list is available by looking at any Scenario classes in the `Scenario/src/main/java/org.terasology.scenario/components/information/IdentificationComponents` directory and looking at any components labeled with `Type` in the name.

##### 3. A ScenarioLogicLabel Component
This component includes the dropdown text for the component, the text is placed in the "name" field of the component.

##### 4. The Indicator Component
This is the component we created before that allows the Scenario system to identify the specific expression.

##### 5. A ScenarioLogicText Component
This component includes the text that is parsed by the Scenario system in order to create our more detailed information of the expression and includes any parameters that need to be satisfied for the expression. This text is include in the "text" field and any parameters should be indicated as the following `[<identifier-for-parameter>:ValueType]` Where ValueType is the type of the parameter, these can include `Integer`, `Item`, `Block`, `Player`, `Region`, `Comparator`, and  `String`.

##### 6. A ScenarioArgumentContainer
This component MUST be included if the ScenarioLogicText component indicates that the expression requires any parameters.


If we take everything from above we can create our prefab file:
`scenarioBlockCountExpression.prefab`
```
{
    "Network":{},
    "ScenarioTypeInteger":{},
    "ScenarioLogicLabel":{
        "name":"Count blocks inventory"
    },
    "ScenarioExpressionBlockCount":{},
    "ScenarioLogicText":{
        "text":"Count of [block:Block] in inventory of [player:Player]"
    },
    "ScenarioArgumentContainer":{}
}
```
For this expression we have two parameters, one that is for the blocktype that we are looking at, and one that indicates which player we want to look at.


### Code Implementation

An expression requires a code implementation in three locations. `ConvertEntitySystem`, `EvaluationDisplaySystem`, and `EvaluationSystem`


#### ConvertEntitySystem

This is the most simple portion of the code and only requires us to indicate to the system how to serialize our expression. For any expression we want to use the default serialization. This means that we need to add to the system by adding a new event listener based on our indicator component.

```java
	@ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent component) {
        DefaultSerialize(event, entity);
    }
```

#### EvaluationDisplaySystem

Now we need to indicate to the system how to display our parameters in conjunction with the text. We do this by implementing a new event listener for our indicator component with the `EvaluateDisplayEvent`. Now, because our expression has parameters we need to first retrieve the mapping of those parameters to their filled in values 

```java 
	@RecieveEvent
    public void onEvaluateIntEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent comp) {
		Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;
    }
```

We have 2 parameters with this expression, the identifiers we gave them were `block` and `player`, so now we need to send an event to evaluate the display of the two values mapped to these identifiers

```java
	EvaluateDisplayEvent evalBlock = new EvaluateDisplayEvent();
    args.get("block").send(evalBlock);
    String itemName = evalBlock.getResult();

	EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
    args.get("player").send(evalPlayer);
    String player = evalPlayer.getResult();
```

And finally we need to set the result of the event to our fully constructed display message.
```java
	event.setResult("Count of " + itemName + " owned by " + player);
```

This results in our full event being:
```java
	@ReceiveEvent
    public void onEvaluateIntEvent(EvaluateDisplayEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent comp) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateDisplayEvent evalBlock = new EvaluateDisplayEvent();
        args.get("block").send(evalBlock);
        String itemName = evalBlock.getResult();

        EvaluateDisplayEvent evalPlayer = new EvaluateDisplayEvent();
        args.get("player").send(evalPlayer);
        String player = evalPlayer.getResult();

        event.setResult("Count of " + itemName + " owned by " + player);
    }
```

#### EvaluationSystem
This is the last section of code and last step needed to implement our expression, but it is also the most important, it's the part that does the actual evaluating of the result. Because our expression evaluates to an Integer we need to trigger on the `EvaluateIntEvent` for our indicator component.
```java
	@ReceiveEvent
    public void onEvaluateCountBlock(EvaluateIntEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent component) {
    }
```

Next, we need to retrieve our parameters, much like the display evaluation.
```java
	Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;
```

Now, instead of evaluating the parameters into strings for display we want to evaluate them into the types they are, this means that we evaluate the `player` parameter to a player type and the `block` parameter to a blockFamily.
```java
	ScenarioValuePlayerComponent.PlayerType player = args.get("player").getComponent(ScenarioValuePlayerComponent.class).type;

	EvaluateBlockEvent evaluateBlockEvent = new EvaluateBlockEvent(event.getPassedEntity());
	args.get("block").send(evaluateBlockEvent);
	BlockFamily blockFamily = evaluateBlockEvent.getResult();
```

Now for the current design of Scenario we just want to double check that the player is the TriggeringPlayer(Currently the only option offered), and if so we want to go through the triggering player's (Passed in the event in the information entity) inventory and sum up all of the counts of that block in their inventory and set our final return result to the value.

For the block count we are going to iterate through the inventory and check the block family of any entities with a BlockComponent, if it matches then we will accumlate the stack count of that block.

```java
	@ReceiveEvent
    public void onEvaluateCountBlock(EvaluateIntEvent event, EntityRef entity, ScenarioExpressionBlockCountComponent component) {
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        ScenarioValuePlayerComponent.PlayerType player = args.get("player").getComponent(ScenarioValuePlayerComponent.class).type;

        EvaluateBlockEvent evaluateBlockEvent = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block").send(evaluateBlockEvent);
        BlockFamily blockFamily = evaluateBlockEvent.getResult();

        int count = 0;

        if (player == ScenarioValuePlayerComponent.PlayerType.TRIGGERING_PLAYER) {
            EntityRef playerEntity = event.getPassedEntity().getComponent(InfoTriggeringEntityComponent.class).entity;
            InventoryComponent  invent = playerEntity.getComponent(InventoryComponent.class);
            for (EntityRef e : invent.itemSlots) {
                if (e.exists() && e.hasComponent(BlockItemComponent.class)) {
                    if (e.getComponent(BlockItemComponent.class).blockFamily.equals(blockFamily)) {
                        count += e.getComponent(ItemComponent.class).stackCount;
                    }
                }
            }
        }

        event.setResult(count);
    }
```


This should now complete our new expression and allow it to be used inside the Scenario Module. Launch the game and check it out for any parameter that needs an integer value!
