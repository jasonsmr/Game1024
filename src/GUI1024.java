import javax.swing.*;
import java.awt.event.*;

public class GUI1024{
    public static void main(String arg[]){

        JFrame gui = new JFrame ("Welcome to 1024!");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GUI1024Panel panel = new GUI1024Panel();
        //panel.setFocusable(true);
        gui.getContentPane().add(panel);
        gui.setJMenuBar(panel.menus);
        gui.setSize(1200,1200);

        gui.setVisible(true);

        panel.resetBoard();

    }
}
