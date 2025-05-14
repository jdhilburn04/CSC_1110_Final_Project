import java.util.Stack;
import java.util.ArrayList;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Stack<Room> history;    
    private Character firstLady;
    private Character garbageMan;
    private Character player;    

    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        firstLady = new Character("First Lady");
        garbageMan = new Character("Garbage Man");
        player = new Character("Player");
        createRooms();
        parser = new Parser();
        history = new Stack<>();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // 8.4
        // Room outside, theater, pub, lab, office;
        Room ovalOffice, vermmeilRoom, library, eastRoom, northEastGate, blueRoom, stateDiningRoom;
      
        // // create the rooms
        ovalOffice = new Room("in the oval office");
        vermmeilRoom = new Room("in the vermmeil room");
        library = new Room("in the white house library");
        eastRoom = new Room("in the east room");
        northEastGate = new Room("outside the white house");
        blueRoom = new Room("in the blue room");
        stateDiningRoom = new Room("in the state dining room");
        
        // // assign images
        ovalOffice.setImage("ovalOffice.jpg");
        vermmeilRoom.setImage("vermeilroom.jpg");
        library.setImage("library.jpg");
        eastRoom.setImage("eastRoom.jpg");
        northEastGate.setImage("northeastGate.jpg");
        blueRoom.setImage("blueroom.jpg");
        stateDiningRoom.setImage("statediningroom.jpg");
        
        // // assign sounds
        // office.setAudio("cricket.mp3");
        // pub.setAudio("bar-chatter.mp3");
        
        // // initialise room exits
        // 8.8
        ovalOffice.setExits("north", eastRoom);
        ovalOffice.setExits("east", vermmeilRoom);
        vermmeilRoom.setExits("north", library);
        vermmeilRoom.setExits("west", ovalOffice);
        library.setExits("south", vermmeilRoom);
        library.setExits("west", eastRoom);
        eastRoom.setExits("north", northEastGate);
        eastRoom.setExits("east", library);
        eastRoom.setExits("south", ovalOffice);
        eastRoom.setExits("west", blueRoom);
        northEastGate.setExits("south", eastRoom);
        blueRoom.setExits("east", eastRoom);
        blueRoom.setExits("west", stateDiningRoom);
        stateDiningRoom.setExits("east", blueRoom);
        
        //8.20
        Item key = new Item("key");
        vermmeilRoom.addItem(key);
        Item garbage = new Item("garbage bag", 12.25, false);
        eastRoom.addItem(garbage);
        Item cocacola = new Item("cocacola", 0.75, true);
        cocacola.setWeightLimitModifier(-5);
        stateDiningRoom.addItem(cocacola);
        Item mcDouble = new Item("McDouble", 0.3, true);
        mcDouble.setWeightLimitModifier(-10);
        stateDiningRoom.addItem(mcDouble);
        Item magicCookie = new Item("magic cookie", 0, true);
        magicCookie.setWeightLimitModifier(100);
        stateDiningRoom.addItem(magicCookie);
        
        northEastGate.lock(key);
        
        firstLady.setRoom(blueRoom);
        firstLady.addItem(cocacola);
        firstLady.setWeightLimit(1);
        garbageMan.setRoom(northEastGate);

        player.setRoom(ovalOffice);  // start game
        player.setWeightLimit(12);
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        println();
        println("Welcome to the White House!");
        println("The first lady asked you to take out the trash... but you forgot. Yikes!");
        println("You need to accomplish the following tasks:");
        println("\n1. Collect the garbage");
        println("\n2. Collect the key");
        println("\n3. Deliver the garbage to the garbage man");
        println("\nType 'help' if you need help.");
        println();
        printLocationInfo();
    }
    
    // 6.5
    private void printLocationInfo()
    {
        // 6.7
        // 6.11
        println(player.getRoom().getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private void processCommand(Command command) 
    {
        if(command.isUnknown()) {
            println("I don't know what you mean...");
            return;
        }

        String commandWord = command.getCommandWord();
        
        switch (commandWord) {
            case "help" -> printHelp();
            case "go" -> goRoom(command);
            case "quit" -> quit(command);
            case "look" -> look(command);
            case "eo" -> eo(command);
            case "back" -> back();
            case "take" -> take(command);
            case "drop" -> drop(command);
            case "eat" -> eat(command);
            case "unlock" -> unlock(command);
        }
    }
    
    private void unlock(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            println("Which door?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = player.getRoom().getExit(direction);
        // 8.6
        if (nextRoom == null) {
            println("There is no door!");
            return;
        }
        
        Item k = player.getItem("key");
        String response = nextRoom.unlock(k);
        println(response);
    }
    
    private Room firstLadyCurrentRoom()
    {
        return firstLady.getRoom();
    }
    
    private Room garbageManCurrentRoom()
    {
        return garbageMan.getRoom();
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        println("You are lost. You are alone. You wander");
        println("around at the White House.");
        println();
        println("Your command words are:");
        println(parser.showCommands());
    }

    /** 
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = player.getRoom().getExit(direction);
        // 8.6
        if (nextRoom == null) {
            println("There is no door!");
            return;
        }
        
        if (nextRoom.isLocked()) {
            println("There door is locked!");
            return;
        }
        
        
        
        history.push(player.getRoom());
        player.setRoom(nextRoom);
        
        // 6.5
        printLocationInfo();
        
        if (firstLadyCurrentRoom() == player.getRoom()) 
        {
            if (garbageManCurrentRoom().getItem("garbage") == null) {
                println("First Lady: 'GO TAKE OUT THAT TRASH!!'");
                return;
            }
            println("First Lady: 'Took you long enough to take the trash out... Geez'");
            return;
        }
        
        if (garbageManCurrentRoom() == player.getRoom())
        {
            if (player.getItem("garbage") == null) {
                println("Garbage Man: 'Hey Mr. President, try eating a magic cookie'");
                return;
            }
            println("Garbage Man: 'Hey Mr. President, you can drop the garbage here for me. Thank you sir!'");
            return;
        }
    }
    
    private void back() {
        if(history.empty()) {
            println("You can't go back any further...");
            println(player.getRoom().getLongDescription());
            return;
        }
        player.setRoom(history.pop());
        println(player.getRoom().getLongDescription());
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private void quit(Command command) 
    {
        if(command.hasSecondWord()) {
            println("Quit what?");
            wantToQuit = false;
        }
        else {
            println("Thank you for playing.  ");
            wantToQuit = true;  // signal that we want to quit
        }
    }
    
    private void look(Command command) {
        println(player.getRoom().getLongDescription());
    }
    
    private void eo(Command command) {
        println("You have signed an executive order");
    }
    
    private void take(Command command)
    {
        if(!command.hasSecondWord()) {
            println("Take what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getRoom().getItem(itemName);

        // Try to leave current room.
        // Room nextRoom = currentRoom.getExit(direction);
        // 8.6
        if (item == null) {
            println("There is no item!");
            return;
        }
        if (player.totalWeight() + item.getWeight() >= player.getWeightLimit()) {
            println("You cannot pick up that item because it's too heavy.");
            return;
        }
        
        player.getRoom().removeItem(itemName);
        player.addItem(item);
        println("You have taken the " + item.getDescription() + ".");
    }
    
    private void drop(Command command)
    {
        if(!command.hasSecondWord()) {
            println("Drop what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.removeItem(itemName);

        if (item == null) {
            println("You don't have that item!");
            return;
        }
        
        player.getRoom().addItem(item);
        println("You dropped the " + item.getDescription() + ".");
        
        if ((garbageMan.getRoom().getItem("garbage")) != null ) {
            println("CONGRATS! YOU HAVE WON THE WHITEHOUSE GAME!");
            return;
        }
        
    }
    
    public void eat(Command command)
    {
        if(!command.hasSecondWord()) {
            println("Eat what?");
            return;
        }
        
        String foodName = command.getSecondWord();
        Item food = player.getItem(foodName);
        
        if (food == null) {
            println("You don't have that food!");
            return;
        }
        
        if (!food.getIsEdible()) {
            println("You can't eat that food!");
            return;
        }
        
        player.setWeightLimit(player.getWeightLimit() + food.getWeightLimitModifier());
        player.removeItem(foodName);
        println("You ate the " + food.getDescription() + ".");
        println("Your current weight limit: " + player.getWeightLimit());
        
    }

    
    /****************************************************************
     * If you want to launch an Applet
     ****************************************************************/
    
    /**
     * @return an Image from the current room
     * @see Image
     */
    public String getImage()
    {
        return player.getRoom().getImage();
    }
    
    /**
     * @return an audio clip from the current room
     * @see AudioClip
     */
    public String getAudio()
    {
        return player.getRoom().getAudio();
    }
    
    /****************************************************************
     * Variables & Methods added 2018-04-16 by William H. Hooper
     ****************************************************************/
    
    private String messages;
    private boolean wantToQuit;
    
    /**
     * Initialize the new variables and begin the game.
     */
    private void start()
    {
        messages = "";
        wantToQuit = false;
        printWelcome();
    }
    
    /**
     * process commands or queries to the game
     * @param input user-supplied input
     */
    public void processInput(String input)
    {
        if(finished()) {
            println("This game is over.  Please go away.");
            return;
        }
        
        Command command = parser.getCommand(input);
        processCommand(command);
    }
    
    /**
     * clear and return the output messages
     * @return current contents of the messages.
     */
    public String readMessages()
    {
        if(messages == null) {
            start();
        }
        String oldMessages = messages;
        messages = "";
        return oldMessages;
    }
    
    /**
     * @return true when the game is over.
     */
    public boolean finished()
    {
        return wantToQuit;
    }

    /**
     * add a message to the output list.
     * @param message the string to be displayed
     */
    private void print(String message)
    {
        messages += message;
    }
    
    /**
     * add a message to the output list, 
     * followed by newline.
     * @param message the string to be displayed
     * @see readMessages
     */
    private void println(String message)
    {
        print(message + "\n");
    }
    
    /**
     * add a blank line to the output list.
     */
    private void println()
    {
        println("");
    }
}
