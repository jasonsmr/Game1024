import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.lang.String;
import java.util.ArrayList;

/**
 * Created by Hans Dulimarta on Summer 2014
 */
public class GUI1024Panel extends JPanel {

    private JLabel[][] gameBoardUI;
    private NumberGameArrayList gameLogic;
    private boolean moved;
    public static JMenuBar menus;
    public JMenu gameMenu;
    private int ROW, COL, WIN;
    public JMenuItem resize, undoEvent, newGame, quitGame;

    public JTextField rows, cols, goal;

    private ButtonListener butListener;

    public GUI1024Panel() {

        gameLogic = new NumberGameArrayList();
        gameLogic.resizeBoard(4, 4, 16);
        butListener = new ButtonListener();
        setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        setLayout(new GridLayout(4, 4));

        gameBoardUI = new JLabel[4][4];

        // Instantiates menu item
        gameMenu = new JMenu("Game");
        newGame = new JMenuItem("New Game");
        undoEvent = new JMenuItem("Undo Move");
        resize = new JMenuItem("Resize");
        quitGame = new JMenuItem("Quit");

        // add selection to the menu
        gameMenu.add(newGame);
        gameMenu.add(undoEvent);
        gameMenu.add(resize);
        gameMenu.add(quitGame);

        // add new menu
        menus = new JMenuBar();
        menus.add(gameMenu);

        // add menu item to the listener
        newGame.addActionListener(butListener);
        undoEvent.addActionListener(butListener);
        resize.addActionListener(butListener);
        quitGame.addActionListener(butListener);

        Font myTextFont = new Font(Font.SERIF, Font.BOLD, 40);
        for (int k = 0; k < gameBoardUI.length; k++)
            for (int m = 0; m < gameBoardUI[k].length; m++) {
                gameBoardUI[k][m] = new JLabel();
                gameBoardUI[k][m].setFont(myTextFont);
                gameBoardUI[k][m].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                gameBoardUI[k][m].setPreferredSize(new Dimension(100, 100));
                add(gameBoardUI[k][m]);
            }

        resetBoard();

        setFocusable(true);
        addKeyListener(new ButtonListener());
    }
    /*******************************************************************
     * This method resets the gameboard using the NumberArray class
     *******************************************************************/
    public void resetBoard(){
        gameLogic.reset();
        updateBoard();
    }

    /*******************************************************************
     * This method does an update to the game board in the cells
     * for the GUI.
     ******************************************************************/
    private void updateBoard() {
        for (JLabel[] row : gameBoardUI)
            for (JLabel s : row) {
                s.setText("");
            }

        ArrayList<Cell> out = gameLogic.getNonEmptyTiles();
        if (out == null) {
            JOptionPane.showMessageDialog(null, "Incomplete implementation getNonEmptyTiles()");
            return;
        }
        for (Cell c : out) {
            JLabel z = gameBoardUI[c.row][c.column];
            z.setText(String.valueOf(Math.abs(c.value)));
            z.setForeground(c.value > 0 ? Color.BLACK : Color.RED);
        }
    }

    /*******************************************************************
     * This method checks if its possible to undo a move backward
     * in the game.
     ******************************************************************/
    private void undoApp() {
        try {
            System.out.println("Attempt to undo");
            gameLogic.undo();
            moved = true;
        } catch (IllegalStateException exp) {
            JOptionPane.showMessageDialog(null, "Can't undo beyond the first move");
            moved = false;
        }
    }



    private class ButtonListener implements KeyListener, ActionListener {
        @Override
        public void keyTyped(KeyEvent e) { }

        @Override
        public void keyPressed(KeyEvent e) {

            moved = false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    try {
                        System.out.println("Attempt to move up");
                    moved = gameLogic.slide(SlideDirection.UP);
                    } catch (IllegalStateException exp) {
                        JOptionPane.showMessageDialog(null, "Wrong Direction!");
                        moved = false;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    try {
                        System.out.println("Attempt to move left");
                    moved = gameLogic.slide(SlideDirection.LEFT);
                    } catch (IllegalStateException exp) {
                        JOptionPane.showMessageDialog(null, "Wrong Direction!");
                        moved = false;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    try {
                        System.out.println("Attempt to move down");
                    moved = gameLogic.slide(SlideDirection.DOWN);
                    } catch (IllegalStateException exp) {
                        JOptionPane.showMessageDialog(null, "Wrong Direction!");
                        moved = false;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    try {
                        System.out.println("Attempt to move right");
                        moved = gameLogic.slide(SlideDirection.RIGHT);
                    } catch (IllegalStateException exp) {
                        JOptionPane.showMessageDialog(null, "Wrong Direction!");
                        moved = false;
                    }
                    break;
                case KeyEvent.VK_U:
                    undoApp();
                    break;
                case KeyEvent.VK_R:
                    int resp1 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Reset Game?",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp1 == JOptionPane.YES_OPTION) {
                            resetBoard();
                    }
                    else {
                        updateBoard();
                    }
            }
            if (moved) {
                updateBoard();
                if (gameLogic.getStatus().equals(GameStatus.USER_WON)){
                    int resp = JOptionPane.showConfirmDialog(null, "You win! play again?", "You Won!",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        resetBoard();
                    } else {
                        System.exit(0);
                    }
                }
                if (gameLogic.getStatus().equals(GameStatus.DIR_ERR)) {
                    int resp1 = JOptionPane.showConfirmDialog(null, "Click OK!", "Wrong direction!",
                            JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (resp1 == JOptionPane.OK_OPTION) {
                        updateBoard();
                    }
                }
                if (gameLogic.getStatus().equals(GameStatus.USER_LOST)) {
                    int resp = JOptionPane.showConfirmDialog(null, "Sorry! play again?", "Game Over!",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        resetBoard();
                    } else {
                        System.exit(0);
                    }
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent e) { }


        /***************************************************************
         * This method determines which button is pressed, and performed the
         * action for each button.
         **************************************************************/
        @Override
        public void actionPerformed(ActionEvent e) {


            // undo to the most recent step, only if undo is possible
            if (e.getSource() == undoEvent) {
                moved = false;
                undoApp();
                updateBoard();
            }

            // quit window
            if (e.getSource() == quitGame)
                System.exit(0);

            // start a new game with same win value and board size
            if (e.getSource() == newGame) {
                int resp1 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Reset Game?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resp1 == JOptionPane.YES_OPTION) {
                    resetBoard();
                }
                else {
                    updateBoard();
                }
            }
        }

        private boolean checkInt(String input){
            boolean isValid = true;
            try
            {
                // checking valid integer using parseInt() method
                int val = Integer.parseInt(input);
            }
            catch (NumberFormatException e)
            {
                isValid = false;
                JOptionPane.showMessageDialog(null, "Enter a real number");
                System.out.println(input + " is not a valid integer number");

            }
            return isValid;
        }

    }

}
