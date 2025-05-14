import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Plays the Game from a Console interface (i.e., a terminal window).
 * 
 * @author William H. Hooper
 * @version 2018-11-29
 */
public class Console
{
    private BufferedReader reader;
    private Game game;

    /**
     * Creates a new console game.
     * @param g a Game, the engine that actually 
     * processes all the input and 
     * generates all the output.
     */
    public Console(Game g)
    {
        reader = new BufferedReader(
            new InputStreamReader(System.in));
        game = g;
    }

    
    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {
        // display the welcome message
        System.out.print('\f');
        String message = game.readMessages();
        sendOutput(message);
        
        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
        while (! game.finished()) {
            String input = getInput();
            game.processInput(input);
            message = game.readMessages();
            sendOutput(message);
        }
        
        sendOutput("Good Bye.\n");
    }
    
    /**
     * Prompts for input and reads one line from the console.
     * @return a single line of input.
     */
    private String getInput()
    {
        String inputLine = "";   // will hold the full input line

        do {
            System.out.print("> ");     // print prompt
            try 
            {
                inputLine = reader.readLine();
            }
            catch(java.io.IOException exc) 
            {
                System.out.println (
                    "There was an error during reading: "
                    + exc.getMessage());
            }
        } while (inputLine.length() == 0);
        
        return inputLine;
    }
    
    /**
     * Prints output in the console window.
     * @param message the message to print.
     * If the message contains embedded newlines,
     * they will appear on successive lines of the console.
     */
    private void sendOutput(String message)
    {
        System.out.print(message);
    }
    
    /**
     * Issue a command and get the response.
     * As a side effect, it prints its output on the terminal
     * in the same form as a game dialog.
     * @param cmd a command such as "help"
     * @return the game output that results from the commmand.
     */
    public String getResponse(String cmd)
    {
        System.out.println(cmd);
        game.processInput(cmd);
        String message = game.readMessages();
        System.out.print(message + "> ");
        return message;
    }   
    
    /**
     * Test whether a given command gets the expected response.
     * Input and output appear in the terminal as a game dialog.
     * @param cmd a command such as "go west"
     * @param reply part of the expected response, e.g., "pub"
     * @return true iff the response contains the reply
     */
    public boolean testCommand(String cmd, String reply)
    {
        String message = getResponse(cmd);
        return message.contains(reply);
    }
    
    /**
     * Test whether a given command gets the expected response.
     * Input and output appear in the terminal as a game dialog.
     * @param cmd a command such as "go west"
     * @return true iff the response contains the reply
     */
    public void testCommand(String cmd)
    {
        testCommand(cmd, "");
    }
}
