import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  The exits are labelled north, 
 * east, south, west.  For each direction, the room stores a reference
 * to the neighboring room, or null if there is no exit in that direction.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */
public class Room 
{
    public String description;
    private String imageName;
    private String audioName;
    private HashMap<String, Room> exits;
    private ArrayList<Item> items;
    private Item key;
    
    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String description) 
    {
        this.description = description;
        exits = new HashMap<>();
        items = new ArrayList<>();
        key = null;
    }
    
    public void lock(Item k)
    {
        key = k;
    }
    
    public boolean isLocked()
    {
        return key != null;
    }
    
    public String unlock(Item k)
    {
        if (k == key) {
            key = null;
            return "This room is unlocked";
        }
        return "This " + k.getDescription() + " does not work";
    }
    
    // 8.6
    public Room getExit(String direction) {
        return (Room)exits.get(direction);
    }
    
    // 8.20
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

    // 8.8
    public String getExitString(){
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for (String key : keys)
        {
            returnString += " " + key;
        }
        return returnString;
    }   
    
    // 8.20
    public String getItemString(){
        String message = "";
        if (items.isEmpty()) {
            return "Nothing to see here.";
        }
        
        for (Item item : items) {
            message += item.getLongDescription();
        }
        
        
        return message;
    }
    
    public void addItem(Item it) {
        items.add(it);
    }
    
    /**
     * Define the exits of this room.  Every direction either leads
     * to another room or is null (no exit there).
     * @param direction
     * @param neighbor
     */
    // 8.8
    public void setExits(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor); //8.8
    }
    
    public String getShortDescription()
    {
        return description;
    }
    
    // 8.11
    public String getLongDescription() {
        String ld = "You are " + description;
        if (!items.isEmpty()) {
            ld += "\nItems: ";
            for (Item item : items) {
                ld += "\n" + item.getLongDescription();
            }
        }
        
        return ld  + ".\n" + getExitString();
    }
    
    /*************************************************************
     * added by William H. Hooper, 2006-11-28
    *************************************************************/
    /**
     * @return a String, which hopefully contains the file name
     * of an Image file.
     */
    public String getImage()
    {
        return imageName;
    }
    
    /**
     * associate an image with this room
     * @param filename a String containing a file.
     * The file <b>must</b> reside in the media directory, 
     * and must be in a format viewable in the Java AWT.
     * Readable formats include: 
     * PNG, JPG (RGB color scheme only), GIF
     */
    public void setImage(String filename)
    {
        imageName = "media/" + filename;
    }
    
    /**
     * @return a String, which hopefully contains the file name
     * of an audio file.
     */
    public String getAudio()
    {
        return audioName;
    }
    
    /**
     * associate an audio clip with this room
     * @param filename a String containing a file.
     * The file <b>must</b> reside in the media directory, 
     * and must be in a format palyable in the Java AWT.
     * Readable formats include: 
     * WAV, AU.
     */
    public void setAudio(String filename)
    {
        audioName = "media/" + filename;
    }
}
