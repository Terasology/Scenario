## Creation of a new Event for the Scenario Module

In this tutorial we will be creating a new Event for the Scenario Module of Terasology.
We will be creating an event that will trigger on a player spawning.

### Secondary Indicator Component

To start off we need to create a new Component that will be used to identify the event For this example we will be naming our new component `ScenarioSecondarySpawnComponent.java`. All events have an indicator component that indicates they are an event, this is our secondary indicator is what allows us to distinguish our specific event vs others.

Now we need to write our component class, for an indicator we need to do two steps, first we need to annotate the class with a `@Replicate` annotation and have it implement the `Component` interface. This results in our class looking like this:

```java
    import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

@Replicate
public class ScenarioSecondarySpawnComponent implements Component {
}
```

### Prefab File

Now that we have our indicator component complete we need to create the `Prefab` file for the event. 

This file needs to be placed inside the module's `<module-name>/assets/prefabs` directory. In this case we will name the file `onPlayerSpawnEvent.prefab`

A scenario event prefab needs to have a few components:
1) A network component
2) A ScenarioIndicatorEvent component
3) A ScenarioLogicLabel component
4) A Secondary Indicator component
5) A ScenarioLogicText component
6) A ScenarioArgumentContainer(If the ScenarioLogicText text has any parameters, detailed below)

##### 1. Network Component
This is always required for every event within the Scenario module, it allows the event to be used on multiplayer.
    
##### 2. An ScenarioIndicatorEvent Component
This is the component that indicates to the system that this prefab is an event, it is required for all events.

##### 3. A ScenarioLogicLabel Component
This component includes the dropdown text for the component, the text is placed in the "name" field of the component.

##### 4. The Secondary Indicator Component
This is the component we created before that allows the Scenario system to identify the specific event.

##### 5. A ScenarioLogicText Component
This component includes the text that is parsed by the Scenario system in order to create our more detailed information of the expression and includes any parameters that need to be satisfied for the expression. This text is include in the "text" field and any parameters should be indicated as the following `[<identifier-for-parameter>:ValueType]` Where ValueType is the type of the parameter, these can include `Integer`, `Item`, `Block`, `Player`, `Region`, `Comparator`, and  `String`.

##### 6. A ScenarioArgumentContainer
This component MUST be included if the ScenarioLogicText component indicates that the event requires any parameters.


If we take everything from above we can create our prefab file:
`onPlayerSpawnEvent.prefab`
```
{
    "Network":{},
    "ScenarioIndicatorEvent":{},
    "ScenarioLogicLabel":{
        "name":"On Player Spawn"
    },
    "ScenarioSecondarySpawn":{},
    "ScenarioLogicText":{
        "text":"On player spawn"
    }
}
```
For this event we have no parameters and therefore don't need the ScenarioArgumentContainer that was mentioned above.


### Code Implementation

An event requires a code implementation in two, possibly three locations. `ConvertEntitySystem` and `ScenarioRootManagementSystem`, an event may also require coce in `EventListeningSystem`


#### ConvertEntitySystem

This is the most simple portion of the code and only requires us to indicate to the system how to serialize our event. For any event we want to use the default serialization. This means that we need to add to the system by adding a new event listener based on our indicator component.

```java
	@ReceiveEvent
    public void onConvertEntityEvent(ConvertScenarioEntityEvent event, EntityRef entity, ScenarioSecondarySpawnComponent component) {
        DefaultSerialize(event, entity);
    }
```

#### ScenarioRootManagementSystem

This is the system that will trigger when our event happens and let us construct the information entity that will be passed to any conditionals/actions. In order to setup the listener we need to look at the event we are waiting for. This will be looking at the event that gets passed to the Scenario entity(the entity that has the ScenarioComponent). The event that we are passing is detailed below:
`PlayerSpawnScenarioEvent.java`
``` java
	import org.terasology.entitySystem.entity.EntityRef;
	import org.terasology.entitySystem.event.Event;
	import org.terasology.scenario.components.events.ScenarioSecondarySpawnComponent;

	public class PlayerSpawnScenarioEvent implements Event {
    	private EntityRef spawningEntity;

   		public PlayerSpawnScenarioEvent(EntityRef spawningEntity) {
        	this.spawningEntity = spawningEntity;
    	}

    	public EntityRef getSpawningEntity() {
        	return spawningEntity;
    	}
	}
```

The passing of the event is detailed below in the `EventListeningSystem` portion.

This is the basic listener we will setup in the `ScenarioRootManagementSystem`

```java
	@ReceiveEvent
    public void onPlayerSpawnScenarioEvent(PlayerSpawnScenarioEvent event, EntityRef entity, ScenarioComponent component) {
    }
```

First, we want to grab all entitites that have our secondary indicator component attached to them, this will allow for us to get any triggers that utilize our event.

```java
	Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondarySpawnComponent.class);
```

And now we need to construct our information entity, in this case for a spawn event the only information we can retrieve is the triggering entity(the player). The information able to be passed is detailed in the components included in `Scenario/src/main/java/org.terasology.scenario/components/events/triggerInformation`. New components may be added to this if you have events that gain more information that needs to be passed to new actions/conditionals that you create.

```java
	InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
    triggerEntity.entity = event.getSpawningEntity();
    EntityRef passEntity = entityManager.create(triggerEntity);
```

Finally we want to pass a new `EventTriggerEvent` to all of the triggers that are activated with our event, while passing them the newly constructed information entity.

```java
	entityList.forEach(e -> e.getOwner().send(new EventTriggerEvent(passEntity)));
```

To get our complete event listener:

```java
	@ReceiveEvent
    public void onPlayerSpawnScenarioEvent(PlayerSpawnScenarioEvent event, EntityRef entity, ScenarioComponent component) {
        Iterable<EntityRef> entityList = entityManager.getEntitiesWith(ScenarioSecondarySpawnComponent.class);
        InfoTriggeringEntityComponent triggerEntity = new InfoTriggeringEntityComponent();
        triggerEntity.entity = event.getSpawningEntity();
        EntityRef passEntity = entityManager.create(triggerEntity);
        entityList.forEach(e -> e.getOwner().send(new EventTriggerEvent(passEntity)));
    }
```


#### EventListeningSystem

In order to listen to our event we need to send an event to the Scenario entity, this often means that we need to construct a listener for the relevent Terasology event and then send the Scenario entity our special event created for the Scenario module. For this event what this means is that we need to setup a listener for the Terasology event `OnPlayerSpawnedEvent` and then pass the Scenario entity our event we created above `PlayerSpawnScenarioEvent`.

In order to do this we just need to setup a listener for the `OnPlayerSpawnedEvent` and then find the Scenario entity and send it our newly created event.

```java
	@ReceiveEvent //Spawn, initial spawn on joining a server
    public void onPlayerSpawnEvent(OnPlayerSpawnedEvent event, EntityRef entity) {
        if (entityManager.getEntitiesWith(ScenarioComponent.class).iterator().hasNext()) {
            EntityRef scenario = entityManager.getEntitiesWith(ScenarioComponent.class).iterator().next();
            if (scenario == null) {
                return;
            }
            scenario.send(new PlayerSpawnScenarioEvent(entity));
        }
    }
```

It is important to notice that we pass the `PlayerSpawnScenarioEvent` the entity that was spawned so that we can properly use it for creating our information entity later when the event gets triggered.

This should now complete our new event and allow it to be used inside the Scenario Module. Launch the game and test out your new event!
