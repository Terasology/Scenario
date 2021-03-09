## Creation of a new Conditional for the Scenario Module

In this tutorial we will be creating a new Conditional for the Scenario Module of Terasology.
We will be creating a conditional that will compare two blocks.

### Secondary Indicator Component

To start off we need to create a new Component that will be used to identify the conditional For this example we will be naming our new component `ScenarioSecondaryBlockCompare.java`. All events have an indicator component that indicates they are an conditional, this is our secondary indicator is what allows us to distinguish our specific conditional vs others.

Now we need to write our component class, for an indicator we need to do two steps, first we need to annotate the class with a `@Replicate` annotation and have it implement the `Component` interface. This results in our class looking like this:

```java
    import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

@Replicate
public class ScenarioSecondaryBlockCompareComponent implements Component {
}
```

### Prefab File

Now that we have our indicator component complete we need to create the `Prefab` file for the event. 

This file needs to be placed inside the module's `<module-name>/assets/prefabs` directory. In this case we will name the file `blockConditional.prefab`

A scenario conditional prefab needs to have a few components:
1) A network component
2) A ScenarioIndicatorConditional component
3) A ScenarioLogicLabel component
4) A Secondary Indicator component
5) A ScenarioLogicText component
6) A ScenarioArgumentContainer(If the ScenarioLogicText text has any parameters, detailed below)

##### 1. Network Component
This is always required for every conditional within the Scenario module, it allows the conditional to be used on multiplayer.
    
##### 2. An ScenarioIndicatorConditional Component
This is the component that indicates to the system that this prefab is an conditional, it is required for all conditionals.

##### 3. A ScenarioLogicLabel Component
This component includes the dropdown text for the component, the text is placed in the "name" field of the component.

##### 4. The Secondary Indicator Component
This is the component we created before that allows the Scenario system to identify the specific conditional.

##### 5. A ScenarioLogicText Component
This component includes the text that is parsed by the Scenario system in order to create our more detailed information of the expression and includes any parameters that need to be satisfied for the expression. This text is include in the "text" field and any parameters should be indicated as the following `[<identifier-for-parameter>:ValueType]` Where ValueType is the type of the parameter, these can include `Integer`, `Item`, `Block`, `Player`, `Region`, `Comparator`, and  `String`.

##### 6. A ScenarioArgumentContainer
This component MUST be included if the ScenarioLogicText component indicates that the conditional requires any parameters.


If we take everything from above we can create our prefab file:
`blockConditional.prefab`
```
{
    "Network":{},
    "ScenarioIndicatorConditional":{},
    "ScenarioLogicLabel":{
        "name":"Block is type"
    },
    "ScenarioSecondaryBlockCompare":{},
    "ScenarioLogicText":{
        "text":"Is [block1:Block] of type [block2:Block]"
    },
    "ScenarioArgumentContainer":{}
}
```
For this event we have two parameters, block1 and block2, and so we need the `ScenarioArgumentContainer` component.


### Code Implementation

A conditional requires code implementation in twoe locations. `ConvertEntitySystem` and `EvaluationSystem`.


#### ConvertEntitySystem

This is the most simple portion of the code and only requires us to indicate to the system how to serialize our conditional. For any conditional we want to use the default serialization. This means that we need to add to the system by adding a new event listener based on our secondary indicator component.

```java
	@ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryBlockCompare component) 		{
        DefaultSerialize(event, entity);
    }
```

#### EvaluationSystem

The evaluation system is where we actually take the passed information from the event that was triggered and evaluate our conditional and verify whether or not the condition is met. We do this by first setting up an event listener for the ConditionalCheckEvent that is watching for entitities with our secondary indicator component.
```java
	@ReceiveEvent
    public void onConditionalCheckEvent(ConditionalCheckEvent event, EntityRef entity, ScenarioSecondaryBlockCompareComponent comp){
    }
```

Next, we need to get our parameter mapping from the `ScenarioArgumentContainer` component, so that we can identify what blocks we want to look at.
```java
	Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;
```

Now that we have the parameter mapping we need to evaluate the blocks using the keys we gave them earlier in the `ScenarioLogicText` component in the prefab file, block1 and block2.

```java
	EvaluateBlockEvent evalBlock1 = new EvaluateBlockEvent(event.getPassedEntity());
    args.get("block1").send(evalBlock1);
    BlockFamily block1 = evalBlock1.getResult();

	EvaluateBlockEvent evalBlock2 = new EvaluateBlockEvent(event.getPassedEntity());
	args.get("block2").send(evalBlock2);
	BlockFamily block2 = evalBlock2.getResult();
```

Lastly, now that we have the block families we need to actually set the return result of the event to the comparison of the blocks.

```java
	event.setResult(block1.equals(block2));
```

In total this results in our conditional evaluation as below:
```java
	@ReceiveEvent
    public void onConditionalCheckEvent(ConditionalCheckEvent event, EntityRef entity, ScenarioSecondaryBlockCompareComponent comp){
        Map<String, EntityRef> args = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateBlockEvent evalBlock1 = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block1").send(evalBlock1);
        BlockFamily block1 = evalBlock1.getResult();

        EvaluateBlockEvent evalBlock2 = new EvaluateBlockEvent(event.getPassedEntity());
        args.get("block2").send(evalBlock2);
        BlockFamily block2 = evalBlock2.getResult();

        event.setResult(block1.equals(block2));
    }
```


This should now complete our new conditional and allow it to be used inside the Scenario Module. Launch the game and test out your new conditional!
