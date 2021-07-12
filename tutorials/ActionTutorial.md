## Creation of a new Action for the Scenario Module

In this tutorial we will be creating a new Action for the Scenario Module of Terasology.
We will be creating an action that will give a player some items.

### Secondary Indicator Component

To start off we need to create a new Component that will be used to identify the action For this example we will be naming our new component `ScenarioSecondaryGiveItemComponent.java`. All actions have an indicator component that indicates they are an action, this is our secondary indicator is what allows us to distinguish our specific action vs others.

Now we need to write our component class, for an indicator we need to do two steps, first we need to annotate the class with a `@Replicate` annotation and have it implement the `Component` interface. This results in our class looking like this:

```java
    import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.engine.network.Replicate;

@Replicate
public class ScenarioSecondaryGiveItemComponent implements Component<ScenarioSecondaryGiveItemComponent> {
}
```

### Prefab File

Now that we have our indicator component complete we need to create the `Prefab` file for the Action. 

This file needs to be placed inside the module's `<module-name>/assets/prefabs` directory. In this case we will name the file `givePlayerItemAction.prefab`

A scenario action prefab needs to have a few components:
1) A network component
2) A ScenarioIndicatorAction component
3) A ScenarioLogicLabel component
4) A Secondary Indicator component
5) A ScenarioLogicText component
6) A ScenarioArgumentContainer(If the ScenarioLogicText text has any parameters, detailed below)

##### 1. Network Component
This is always required for every action within the Scenario module, it allows the action to be used on multiplayer.
    
##### 2. An ScenarioIndicatorAction Component
This is the component that indicates to the system that this prefab is an action, it is required for all actions.

##### 3. A ScenarioLogicLabel Component
This component includes the dropdown text for the component, the text is placed in the "name" field of the component.

##### 4. The Secondary Indicator Component
This is the component we created before that allows the Scenario system to identify the specific action.

##### 5. A ScenarioLogicText Component
This component includes the text that is parsed by the Scenario system in order to create our more detailed information of the action and includes any parameters that need to be satisfied for the action. This text is include in the "text" field and any parameters should be indicated as the following `[<identifier-for-parameter>:ValueType]` Where ValueType is the type of the parameter, these can include `Integer`, `Item`, `Block`, `Player`, `Region`, `Comparator`, and  `String`.

##### 6. A ScenarioArgumentContainer
This component MUST be included if the ScenarioLogicText component indicates that the action requires any parameters.


If we take everything from above we can create our prefab file:
`givePlayerItemAction.prefab`
```
{
    "Network":{},
    "ScenarioIndicatorAction":{},
    "ScenarioLogicLabel":{
        "name":"Give item"
    },
    "ScenarioSecondaryGiveItem":{},
    "ScenarioLogicText":{
        "text":"Give [player:Player] [amount:Integer] [item:Item]."
    },
    "ScenarioArgumentContainer":{}
}
```
For this action we have three parameters, one indicates which player we are giving items, an integer that has how many of that item, and the last is the item itself.


### Code Implementation

An action requires a code implementation in two locations. `ConvertEntitySystem` and `ActionEventSystem`


#### ConvertEntitySystem

This is the most simple portion of the code and only requires us to indicate to the system how to serialize our action. For any action we want to use the default serialization. This means that we need to add to the system by adding a new event listener based on our indicator component.

```java
	@ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondaryGiveItemComponent component) {
        DefaultSerialize(event, entity);
    }
```

#### ActionEventSystem

This is the system that will actually evaluate our action when it is triggered, in order to setup our action we need to create a new event listener that will trigger off a `EventTriggerEvent` for an entity that has our new `ScenarioSecondaryGiveItemComponent`

```java
	@ReceiveEvent //Give Item
    public void onEventTriggerEvent(EventTriggerEvent event, EntityRef entity, ScenarioSecondaryGiveItemComponent action) {
    }
```

Now we need to grab the values from our Argument container that has all of the parameters we setup in our `ScenarioLogicText` component in the Prefab.

```java
	Map<String, EntityRef> variables = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;
```

With this map we can evaluate the parameters based on the keys we gave them in order to use them for actually carrying out the action.

```java
	EvaluateItemPrefabEvent itemEvaluateEvent = new EvaluateItemPrefabEvent(event.informationEntity);
    variables.get("item").send(itemEvaluateEvent);
    Prefab itemPrefab = itemEvaluateEvent.getResult();

	EvaluateIntEvent intEvaluateEvent = new EvaluateIntEvent(event.informationEntity);
    variables.get("amount").send(intEvaluateEvent);
    int amount = intEvaluateEvent.getResult();

	ScenarioValuePlayerComponent.PlayerType player = variables.get("player").getComponent(ScenarioValuePlayerComponent.class).type;	
```

Now that we have all of our evaluated values we want to actually follow through with our action and give the player the correct number of items
```java
	for (int i = 0; i < amount; i++) {
    	EntityRef item = entityManager.create(itemPrefab);


		if (player == ScenarioValuePlayerComponent.PlayerType.TRIGGERING_PLAYER) {
    		EntityRef giveEntity = event.informationEntity.getComponent(InfoTriggeringEntityComponent.class).entity;
    		GiveItemEvent giveItemEvent = new GiveItemEvent(giveEntity);
        	item.send(giveItemEvent);
        }
    }
```


Our overall event listener:
```java
	@ReceiveEvent //Give Item
    public void onEventTriggerEvent(EventTriggerEvent event, EntityRef entity, ScenarioSecondaryGiveItemComponent action) {
        Map<String, EntityRef> variables = entity.getComponent(ScenarioArgumentContainerComponent.class).arguments;

        EvaluateItemPrefabEvent itemEvaluateEvent = new EvaluateItemPrefabEvent(event.informationEntity);
        variables.get("item").send(itemEvaluateEvent);
        Prefab itemPrefab = itemEvaluateEvent.getResult();

        EvaluateIntEvent intEvaluateEvent = new EvaluateIntEvent(event.informationEntity);
        variables.get("amount").send(intEvaluateEvent);
        int amount = intEvaluateEvent.getResult();

        ScenarioValuePlayerComponent.PlayerType player = variables.get("player").getComponent(ScenarioValuePlayerComponent.class).type;

        for (int i = 0; i < amount; i++) {
            EntityRef item = entityManager.create(itemPrefab);


            if (player == ScenarioValuePlayerComponent.PlayerType.TRIGGERING_PLAYER) {
                EntityRef giveEntity = event.informationEntity.getComponent(InfoTriggeringEntityComponent.class).entity;
                GiveItemEvent giveItemEvent = new GiveItemEvent(giveEntity);
                item.send(giveItemEvent);
            }
        }
    }
```


This should now complete our new action and allow it to be used inside the Scenario Module. Launch the game and test out your new action!
