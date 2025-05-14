/**
 * A controller class for the game of Zuul.
 * 
 * @author William H. Hooper
 * @version 2010-04-19
 */
public class whiteHouse
{
    /**
     *  Main play routine.  Loops until end of play.
     */
    public static void main(String args[]) 
    {
        play();
    }
    
    public static void play() 
    {
        Game game = new Game();
        Console viewer = new Console(game);
        
        viewer.play();
    }
    
    // declare constructor private
    // to ensure static invocation
    private whiteHouse() {}
}
