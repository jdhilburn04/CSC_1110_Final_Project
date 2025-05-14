import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * GameTest - a class to test the zuul game engine.
 *
 * @author  William H. Hooper
 * @version 2018-11-19
 */
public class GameTest
{
    private Game game1;
    private Console console1;
    private WHApplet wApp1;

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        System.out.println("\f");
        game1 = new Game();
        console1 = new Console(game1);
        String message = game1.readMessages();
        System.out.print(message + "> ");
        wApp1 = new WHApplet();
        wApp1.begin();
    }

    @Test
    public void start()
    {
        assertEquals(false, game1.finished());
    }

    @Test
    public void map()
    {
        assertEquals(true, console1.testCommand("go east", "vermmeil"));
        assertEquals(true, console1.testCommand("go north", "library"));
        assertEquals(true, console1.testCommand("go west", "east"));
        assertEquals(true, console1.testCommand("go west", "blue"));
        assertEquals(true, console1.testCommand("go west", "dining"));
    }
    
    @Test
    public void takeKey()
    {
        assertEquals(true, console1.testCommand("go east", "vermmeil"));
        assertEquals(true, console1.testCommand("take key", "taken"));
        assertEquals(false, console1.testCommand("look", "key"));
    }
    
    @Test
    public void locked()
    {
        assertEquals(true, console1.testCommand("go north", "east"));
        assertEquals(true, console1.testCommand("go north", "locked"));
        assertEquals(true, console1.testCommand("look", "east"));
    }
    
    @Test
    public void unlocked()
    {
        assertEquals(true, console1.testCommand("go east", "vermmeil"));
        assertEquals(true, console1.testCommand("take key", "taken"));
        assertEquals(true, console1.testCommand("back", "oval"));
        assertEquals(true, console1.testCommand("go north", "east"));
        assertEquals(true, console1.testCommand("go north", "locked"));
        assertEquals(true, console1.testCommand("unlock north", "unlocked"));
        assertEquals(false, console1.testCommand("go north", "outside"));
    }
    
    @Test
    public void noGarbage()
    {
        assertEquals(true, console1.testCommand("go east", "vermmeil"));
        assertEquals(true, console1.testCommand("take key", "taken"));
        assertEquals(true, console1.testCommand("back", "oval"));
        assertEquals(true, console1.testCommand("go north", "east"));
        assertEquals(true, console1.testCommand("unlock north", "unlocked"));
        assertEquals(true, console1.testCommand("go north", "cookie"));
        assertEquals(true, console1.testCommand("go south", "east"));
        assertEquals(true, console1.testCommand("go west", "TRASH!"));
    }
    
    @Test
    public void dropGarbage()
    {
        assertEquals(true, console1.testCommand("go east", "vermmeil"));
        assertEquals(true, console1.testCommand("take key", "taken"));
        assertEquals(true, console1.testCommand("back", "oval"));
        assertEquals(true, console1.testCommand("go north", "east"));
        assertEquals(true, console1.testCommand("go west", "blue"));
        assertEquals(true, console1.testCommand("go west", "dining"));
        assertEquals(true, console1.testCommand("take cookie", "taken"));
        assertEquals(true, console1.testCommand("eat cookie", "ate"));
        assertEquals(true, console1.testCommand("back", "blue"));
        assertEquals(true, console1.testCommand("back", "east"));
        assertEquals(true, console1.testCommand("take garbage bag", "taken"));
        assertEquals(true, console1.testCommand("unlock north", "unlocked"));
        assertEquals(true, console1.testCommand("go north", "Garbage Man:"));
        assertEquals(true, console1.testCommand("drop garbage bag", "CONGRATS!"));
        assertEquals(true, console1.testCommand("back", "east"));
        assertEquals(true, console1.testCommand("go west", "Geez"));
    }

    @Test
    public void noDoor()
    {
        assertEquals(true, console1.testCommand("go north", "no door!"));
    }

    @Test
    public void quit()
    {
        console1.testCommand("quit");
        assertEquals(true, game1.finished());
        assertEquals(false, console1.testCommand("go North", "doorway"));
        assertEquals(true, console1.testCommand("anything", "game is over"));
    }

    @Test
    public void theater()
    {
        assertEquals(true, console1.testCommand("go east", "theater"));
    }

    @Test
    public void office()
    {
        console1.testCommand("go south");
        assertEquals(true, console1.testCommand("go east", "office"));
    }

    @Test
    public void help()
    {
        String string1 = console1.getResponse("help");
        assertNotNull(string1);
        assertEquals(true, string1.contains("go"));
        assertEquals(true, string1.contains("quit"));
        assertEquals(true, string1.contains("help"));
    }
    
    /********************************************************************************
     * The following tests are of interest to multimedia developers.
     * If you are developing a text-only game, comment them out or 
     * delete them altogether.
     ********************************************************************************/

    @Test
    public void pubChatter()
    {
        assertEquals(true, wApp1.testCommand("go west", "pub", 
                "cozy-little-pub", "bar-chatter"));
    }

    @Test
    public void leavePub()
    {
        wApp1.setDelay(3);
        assertEquals(true, wApp1.testCommand("go west", "pub", 
                "cozy-little-pub", "bar-chatter"));
        assertEquals(true, wApp1.testCommand("go east", "outside"));
        assertEquals("", wApp1.getAudio());
    }

    @Test
    public void cricket()
    {
        assertEquals(true, wApp1.testCommand("go south", "lab", "computer-lab"));
        assertEquals(true, wApp1.testCommand("go east", "office", "cluttered-office",
                "cricket"));
    }

    @Test
    public void mapMedia()
    {
        assertEquals(true, wApp1.testCommand("go east", "theater", "lecture-hall"));
        assertEquals(true, wApp1.testCommand("go west", "outside", "deakinsign"));
        assertEquals(true, wApp1.testCommand("go west", "pub", "cozy-little-pub",
                "bar-chatter"));
        assertEquals(true, wApp1.testCommand("go east", "outside"));
        assertEquals(true, wApp1.testCommand("go south", "lab", "computer-lab"));
        assertEquals(true, wApp1.testCommand("go east", "office", "cluttered-office",
                "cricket"));
        assertEquals(true, wApp1.testCommand("go west", "lab"));
        assertEquals(true, wApp1.testCommand("go north", "outside"));
    }

    // @Test
    // public void mapImages()
    // {
        // assertEquals(true, zApp1.testCommand("go east", "theater", "lecture-hall"));
        // assertEquals(true, zApp1.testCommand("go west", "outside", "deakinsign"));
        // assertEquals(true, zApp1.testCommand("go west", "pub", "cozy-little-pub"));
        // assertEquals(true, zApp1.testCommand("go east", "outside"));
        // assertEquals(true, zApp1.testCommand("go south", "lab", "computer-lab"));
        // assertEquals(true, zApp1.testCommand("go east", "office", 
        //         "cluttered-office"));
        // assertEquals(true, zApp1.testCommand("go west", "lab"));
        // assertEquals(true, zApp1.testCommand("go north", "outside"));
    // }

    // @Test
    // public void mapText()
    // {
        // assertEquals(true, zApp1.testCommand("go east", "theater"));
        // assertEquals(true, zApp1.testCommand("go west", "outside"));
        // assertEquals(true, zApp1.testCommand("go west", "pub"));
        // assertEquals(true, zApp1.testCommand("go east", "outside"));
        // assertEquals(true, zApp1.testCommand("go south", "lab"));
        // assertEquals(true, zApp1.testCommand("go east", "office"));
        // assertEquals(true, zApp1.testCommand("go west", "lab"));
        // assertEquals(true, zApp1.testCommand("go north", "outside"));
    // }

    // @Test public void fillOutput()
    // {
        // zApp1.setDelay(0);
        // for(int i = 0; i < 10; i++) {
            // mapText();
        // }
    // }

    // @Test public void fillHistory()
    // {
        // zApp1.setDelay(0);
        // for(int i = 0; i < 100; i++) {
            // mapText();
        // }
    // }

    @Test
    public void testCommand()
    {
        assertEquals(true, console1.testCommand("go north", "east"));
    }

    @Test
    public void whiteHouseFinishedFalse()
    {
    }

    @Test
    public void whiteHouseFinishedTrue()
    {
    }

    @Test
    public void testHelp()
    {
    }

    @Test
    public void NoDoor827()
    {
    }

    @Test
    public void Items()
    {
    }

    @Test
    public void testBack()
    {
    }

    @Test
    public void testLook()
    {
    }

    @Test
    public void testExecutiveOrder()
    {
    }
}










