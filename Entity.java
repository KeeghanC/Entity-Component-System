import java.util.Map;
import java.util.HashMap;

public class Entity {
    // Components
    private Map<Class, Component> mComponents;

    // Add Component of given type
    public void addComponent(Component component){
        mComponents.put(component.getClass(),component);
    }

    // Check if Entity has Component of given type
    public Boolean hasComponent(Class type) {
        return mComponents.containsKey(type);
    }

    // Get Component of given type
    public Component getComponent(Class type) {
        return mComponents.get(type);
    }

    // Constructor
    public Entity() {
        // Entity Components
        mComponents = new HashMap<Class, Component>();
    }
}
