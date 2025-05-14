import java.util.ArrayList;
/**
 * Write a description of class Player here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Character
{
    private String characterName;
    private Room currentRoom;
    private ArrayList<Item> items;
    private double limit;

    
    public Character(String name)
    {
        // this.name = name;
        characterName = name;
        currentRoom = null;
        limit = 0;
        items = new ArrayList<>();
    }
    
    public void setRoom(Room r)
    {
        currentRoom = r;
    }
    
    public Room getRoom()
    {
        return currentRoom;
    }
    
    public Item getItem(String it) {
        for (Item item : items) {
            if (item.getLongDescription().contains(it)) {
                return item;
            }
        }
        return null;
    }
    
    public Item removeItem(String it) {
        for (Item item : items) {
            if (item.getLongDescription().contains(it)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }
    
    public void addItem(Item item) {
        items.add(item);
    }

    public double getWeight(String item)
    {
        double totalInventoryWeight = 0;
        for (Item i : items) {
            if (i.getLongDescription().contains(item)) {
                double currentInventoryWeight = i.getWeight();
                totalInventoryWeight += currentInventoryWeight;
            }
        }
        return totalInventoryWeight;
    }
    
    public double totalWeight()
    {
        double sum = 0;
        for (Item i : items) {
            sum += i.getWeight();
        }
        return sum;
    }
    
    public void setWeightLimit(double l)
    {
        limit  = l;
    }
    
    public double getWeightLimit()
    {
        return limit;
    }
        
}
