import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class zuulApplet - a View + Controller for zuul
 * 
 * @author William H.Hooper
 * @version 2018-04-16
 */
public class WHApplet 
extends JApplet
implements ActionListener
{
    private JTextArea output;
    private JTextField input;
    private JScrollPane scrollPane;
    Game game;
    String history;
    Image image;
    MusicPlayer player;
    String currentImageFilename;
    String currentAudioFilename;
    double delay;

    /**
     * Called by the browser or applet viewer to inform this JApplet that it
     * has been loaded into the system.It is always called before the first 
     * time that the start method is called.
     */
    public void init()
    {
        output = new JTextArea(1, 1);
        output.setEditable(false);

        scrollPane = new JScrollPane(output);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        input = new JTextField();
        input.addActionListener(this);
        add(input);
        delay = 0.5;
    }

    /**
     * Called by the browser or applet viewer to inform this JApplet that it 
     * should start its execution.It is called after the init method and 
     * each time the JApplet is revisited in a Web page.
     */
    public void start()
    {
        setLayout(null);
        setBackground(new Color(50, 0, 50));
        scrollPane.setBounds(10, getHeight()/2, 
            getWidth()-20, getHeight()/2-40);
        input.setBounds(10, getHeight()-30, getWidth()-20, 20);

        game = new Game();
        playAudio();
        setImage();
        history = game.readMessages();
        output.setText(history);
        input.requestFocusInWindow();
        repaint();
    }

    /** 
     * Called by the browser or applet viewer to inform this JApplet that
     * it should stop its execution.It is called when the Web page that
     * contains this JApplet has been replaced by another page, and also
     * just before the JApplet is to be destroyed.
     */
    public void stop()
    {
        // provide any code that needs to be run when page
        // is replaced by another page or before JApplet is destroyed 
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == input)
        {
            String inputLine = input.getText();
            respond(inputLine);
        }
    }

    /**
     * Respond to a command as though it had been entered 
     * into the input field
     * @param inputLine A line you would type into the input field
     */
    public void respond(String inputLine)
    { 
        input.setText("");

        history += "> " + inputLine + "\n";
        game.processInput(inputLine);
        playAudio();
        setImage();

        history += game.readMessages();

        // put the last few lines of history into the output field
        int maxWindow = 5000;
        int endIndex = history.length();
        int startIndex = Math.max(0, endIndex - maxWindow);
        String window = history.substring(startIndex, endIndex);
        output.setText(window);
        output.setCaretPosition(window.length());

        // prevent overflow of the internal history buffer
        int maxHistory = 2 * maxWindow;
        int length = history.length();
        if(length > maxHistory) {
            int trim = length - maxWindow;
            history = history.substring(trim, length);
            length = history.length();
        }

        repaint();
    }

    public String getLastResponse()
    {
        String[] lines = history.split("\n");
        int endIndex = lines.length;
        int startIndex = endIndex - 1;
        for( ; startIndex >= 0; startIndex--) {
            String startLine = lines[startIndex];
            if(startLine.charAt(0) == '>') {
                break;
            }
        }
        String[] slice = Arrays.copyOfRange(lines, startIndex, endIndex);
        String rejoined = String.join("\n", slice);
        return rejoined;
    }

    public String getHistory(int nLines)
    {
        String[] lines = history.split("\n");
        int endIndex = lines.length;
        int startIndex = Math.max(0, endIndex - nLines);
        String[] slice = Arrays.copyOfRange(lines, startIndex, endIndex);
        String rejoined = String.join("\n", slice);
        return rejoined;
    }

    private void setImage()
    {
        currentImageFilename = null;

        String filename = game.getImage();
        if(filename == null) {
            return;
        }

        Class<? extends JApplet> appClass = null;
        URL url = null;
        ImageIcon icon = null;
        try {
            appClass = getClass();
            url = appClass.getResource(filename);
            icon = new ImageIcon(url);
            image = icon.getImage();
            currentImageFilename = filename;
        } catch (Exception e) { 
            System.err.println("setImage(): " + e);
            System.err.println("filename = " + filename);
            System.err.println("appClass = " + appClass);
            System.err.println("     url = " + url);
            System.err.println("    icon = " + icon);
            image = null;
        }
    }

    private void playAudio()
    {
        String filename = game.getAudio();
        if(filename == currentAudioFilename) {
            return;
        };

        currentAudioFilename = null;
        if(player != null) {
            player.stop();
        }
        if(filename == null) {
            return;
        }

        if(player == null) {
            player = new MusicPlayer();
        }
        player.startPlaying(filename);
        if(player.isPlaying()) {
            currentAudioFilename = filename;
        }
    }
    
    public void setDelay(double d)
    {
        delay = d;
    }

    /**
     * Pause the applet
     * @param seconds the amount of time to sleep
     */
    private void sleep(double seconds)
    {
        int milliSeconds = (int)(seconds * 1000);

        // delay so you can see the image
        try {
            Thread.sleep(milliSeconds);
        } catch(Exception e) {}
    }

    /**
     * Test whether a given command gets the expected response.
     * Input and output appear in the terminal as a game dialog.
     * @param cmd a command such as "go west"
     * @param reply part of the expected response, e.g., "pub"
     * @param image part of the current image filename, 
     *  e.g., "cozy-little-pub"
     * @param audio part of the current audio filename, 
     *  e.g., "crickets"
     * @return true iff all the expected  contains the reply
     */
    public boolean testCommand(String cmd, String reply
    , String image, String audio)
    {
        respond(cmd);
        String message = getLastResponse();
        if(!message.contains(reply)) {
            return false;
        }

        String imagefile = getImage();
        if(!imagefile.contains(image)) {
            return false;
        }

        String audiofile = getAudio();
        if(!audiofile.contains(audio)) {
            return false;
        }

        // allow the user a moment to view image and hear audio
        sleep(delay);

        return true;
    }

    public boolean testCommand(String cmd, String reply
    , String image) 
    { return testCommand(cmd, reply, image, ""); }

    public boolean testCommand(String cmd, String reply)
    { return testCommand(cmd, reply, "", ""); }

    public void testCommand(String cmd)
    { testCommand(cmd, "", "", ""); }

    /**
     * @return the filename of the image currently displayed, 
     *  or the empty String ""
     */
    public String getImage()
    {
        if(image == null) {
            return "";
        }

        if(currentImageFilename == null) {
            return "";
        }
        return currentImageFilename;
    }

    /**
     * @return the filename of the audio currently playing, 
     *  or the empty String ""
     */
    public String getAudio()
    {   
        if(currentAudioFilename == null) {
            return "";
        }

        if(!player.isPlaying()) {
            return "";
        }

        return currentAudioFilename;
    }

    /**
     * Paint method for applet.
     * 
     * @param  g   the Graphics object for this applet
     */
    public void paint(Graphics g)
    {
        g.fillRect(0, 0, getWidth(), getHeight());
        output.repaint();
        input.repaint();
        paintImage(g);
    }

    private void paintImage(Graphics g)
    {
        setImage();
        if(image == null) {
            g.setColor(Color.white);
            g.setFont(new Font("Serif", Font.BOLD, 24));
            g.drawString("???", getWidth() / 2 - 12, getHeight() / 3 - 12);
            return;
        }

        // image original height and width
        int ow = image.getWidth(null);
        int oh = image.getHeight(null);

        // fit the image to the window
        int fw = Math.min(ow, getWidth() - 50);
        int fh = Math.min(oh, getHeight()/2 - 50);

        // scale the image to correct proportion
        int w =  Math.min(fw, ow * fh / oh);
        int h =  Math.min(fh, oh * fw / ow);

        // center the image in at the top of the screen
        int x = (getWidth() - w) / 2;
        int y = (getHeight()/2 - h) / 2;

        g.drawImage(image, x, y, w, h, this);
    }

    public static void play() {
        WHApplet applet = new WHApplet();
        applet.begin();
    }

    public void begin()
    {
        WHApplet applet = this;
        int width = 500;
        int height = 600;
        String title = "zuul";
        try {
            // https://stackoverflow.com/a/27871038/2619926
            Process p = Runtime.getRuntime().exec(new String[] 
                    {"sh", "-c", "basename \"$PWD\""});
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
            title = reader.readLine();
            // System.err.println(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height + 20);
        applet.setSize(width, height);
        frame.add(applet);
        applet.init();      // simulate browser call(1)
        applet.start();      // simulate browser call(2)
        frame.setVisible(true);
    } 

    public static void main(String[] argv) 
    { 
        play(); 
    }
}
