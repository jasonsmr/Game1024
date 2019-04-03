import java.util.*;

import javax.swing.text.Element;

/***********************************************************************
 * This class will have all basic functions of a 1024 game. It will be able to
 * perform shifting tiles, undo moves, return game states, and formating board.
 *
 * @author Jason Rupright
 * @version 09-05-2018
 **********************************************************************/
public class NumberGameArrayList implements NumberSlider {

    /** A 2-D array for gameBoard */
    private int[][] gameBoard;

    /** Temp array used to check the changes in array shift */
    private int[][] temp;

    /** The winning number, number rows, columns, and total scores */
    private int WIN, ROWS, COLS;

    /** Placeholder for which direction array is moved */
    int isEdge = 0;

    /** Current status of the game lose, win, or in progress */
    private GameStatus status;

    /** A stack moves all past moves */
    private Stack<int[][]> moves;

    /** Global boolean variable whether moved cell, and whether moving in reverse */
    private boolean reverse, emptyFound, moved, newGame = true, moveERR = false;

    /** Random variable for random numbers */
    private Random rand;

    /*******************************************************************
     * This is the main constructor
     ******************************************************************/
    public NumberGameArrayList() {
        rand = new Random();
        moves = new Stack<int[][]>();
    }

    /*******************************************************************
     * Reset the game logic to handle a board of a given dimension
     *
     * @param rows
     *            the number of rows in the board
     * @param columns
     *            the number of columns in the board
     * @param goal
     *            such as 1024 or 2048
     * @throws IllegalArgumentException
     *             when winning value is not power of two or negative
     ******************************************************************/
    @Override
    public void resizeBoard(int rows, int columns, int goal) {

        if(rows < 0 || columns < 0)
            throw new ArrayIndexOutOfBoundsException();
        for(int i = 0; i != -1; i++) {
            if (goal == Math.pow(2, i)) {
                WIN = goal;
                i = -2;
            } else if (goal < Math.pow(2, i))
                throw new IllegalArgumentException();
        }
        ROWS = rows;
        COLS = columns;
        WIN = goal;

        gameBoard = new int[ROWS][COLS];
        temp = new int[ROWS][COLS];
        newGame = true;
    }

    /*******************************************************************
     * @return numbers of rows in the board
     ******************************************************************/
    @Override
    public int getRows() {
        return gameBoard.length;
    }

    /*******************************************************************
     * @return numbers of columns in the board
     ******************************************************************/
    @Override
    public int getCols() {
        return gameBoard[0].length;
    }

    /*******************************************************************
     * Fill gameBoard with zeros clearing the board then place two at
     * random location (2 or 4)
     ******************************************************************/
    @Override
    public void reset() {
        for(int i = 0; i < ROWS; i++)
            for(int j = 0; j < COLS; j++)
                gameBoard[i][j] = 0;

        moves.removeAllElements();
        newGame = true;
        placeRandomValue();
        newGame = true;
        placeRandomValue();
    }

    /*******************************************************************
     * Given a 2 dimensional array of integers fills game board.
     *
     * @param input
     *            2 dimensional array of integers
     * @throws IllegalArgumentException
     *            size is greater than the current board
     ******************************************************************/
    @Override
    public void setValues(int[][] input) {

        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                gameBoard[i][j] = input[i][j];
            }
        }
    }

    /*******************************************************************
     * Places a random 2 or 4 in the gameBoard array
     *
     * @return a Cell object filled with row, column, and values
     *
     * @throws IllegalStateException
     *             when the board has no empty cell
     ******************************************************************/
    @Override
    public Cell placeRandomValue() {
        emptyFound = false;
        int twoFour = Math.abs(rand.nextInt(4/2)*2 + 2);
        Cell cell = new Cell();
        int rowRnd;
        int colRnd;
        //throws IllegalStateException when the board has no empty cell
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col< COLS; col++) {
                if (gameBoard[row][col] == 0) {
                    emptyFound = true;
                    break;
                }
                if(getNonEmptyTiles().size() == gameBoard.length * gameBoard[0].length)
                    throw new IllegalStateException();
            }
        do {
            rowRnd = rand.nextInt(gameBoard.length);
            colRnd = rand.nextInt(gameBoard[0].length);

        } while (gameBoard[rowRnd][colRnd] != 0);

// NOTE: Please implement properly for possible fix;
// (for programmer desired interpertation of the game if implemented);
// It can create a popup informing user of no more possible
// moves in indicated direction.
//        if(!emptyFound && !moved)
//            if(this.getStatus().equals(GameStatus.DIR_ERR))
//                moveERR = true;
        if(emptyFound && newGame && !moved)
            gameBoard[rowRnd][colRnd] = 2;
        else
            gameBoard[rowRnd][colRnd] = twoFour;

        cell.row = rowRnd;
        cell.column = colRnd;
        cell.value = gameBoard[rowRnd][colRnd];
        return cell;
    }

    /*******************************************************************
     * Slides all the tiles to the requested direction
     *
     * @param dir
     *            the direction the tiles is moving to
     * @return true when the tiles is moved
     ******************************************************************/
    @Override
    public boolean slide(SlideDirection dir) {
        //ArrayList<Integer> merge = new ArrayList<Integer>();
        //ArrayList<Integer> zeros = new ArrayList<Integer>();
        moved = false;
        int[][] merge;
        merge = new int[ROWS][COLS];
//        moves.push(ArrayList<Cell>);
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                merge[row][col] = gameBoard[row][col];
            }
        }

        switch (dir) {

            case UP:
                moved = false;
                for (int col = 0; col < COLS; col++) {
                    isEdge = 0;
                    for (int row = 0; row < ROWS; row++) {
                        // return false if board is full and no moves
                        temp[row][col] = gameBoard[row][col];
//                        if(this.getStatus().equals(GameStatus.DIR_ERR) || moveERR){
//                            moveERR = true;
//                        }
                        if (gameBoard[row][col] != 0) {
                            // if we had a prior zero tile then shift the column
                            if (isEdge <= row) {
                                rowShift(row, col, false);
                                if (gameBoard[row][col] != temp[row][col])
                                    moved = true;
                            }
                        }
                    }
                }
                break;


            case DOWN:
                moved = false;
                for (int col = 0; col < COLS; col++) {
                    isEdge = ROWS - 1;
                    for (int row = ROWS -1; row >= 0; row--) {
                        // return false if board is full and no moves
                        temp[row][col] = gameBoard[row][col];
//                        if(this.getStatus().equals(GameStatus.DIR_ERR) || moveERR){
//                            moveERR = true;
//                        }
                        if (gameBoard[row][col] != 0) {
                            // if we had a prior zero tile then shift the column
                            if (isEdge >= row) {
                                rowShift(row, col, true);
                                if (gameBoard[row][col] != temp[row][col])
                                    moved = true;
                            }
                        }
                    }
                }
                break;

            case LEFT:
                moved = false;
                for (int row = 0; row < ROWS; row++) {
                    isEdge = 0;
                    for (int col = 0; col < COLS; col++) {
                        // return false if board is full and no moves
                        temp[row][col] = gameBoard[row][col];
//                        if(this.getStatus().equals(GameStatus.DIR_ERR) || moveERR){
//                            moveERR = true;
//                        }
                        if (gameBoard[row][col] != 0) {
                            // if we had a prior zero tile then shift the row
                            if (isEdge <= col) {
                                colShift(row, col, false);
                                if (gameBoard[row][col] != temp[row][col])
                                    moved = true;
                            }
                        }
                    }
                }
                break;

            case RIGHT:
                moved = false;
                for (int row = 0; row < ROWS; row++) {
                    isEdge = COLS - 1;
                    for (int col = COLS - 1; col >= 0; col--) {
                        // return false if board is full and no moves
                        temp[row][col] = gameBoard[row][col];
//                        if(this.getStatus().equals(GameStatus.DIR_ERR) || moveERR){
//                            moveERR = true;
//                        }
                        if (gameBoard[row][col] != 0) {
                            // if we had a prior zero tile then shift the row
                            if (isEdge >= col) {
                                colShift(row, col, true);
                                if (gameBoard[row][col] != temp[row][col])
                                    moved = true;
                            }
                        }
                    }
                }
                break;

        }
        // stores board and place a random value if the board is moved
        if (moved) {
            newGame = false;
            moves.add(merge);
            placeRandomValue();
        }
        return moved;
    }


    /*******************************************************************
     * Credit for these two methods are given to stack exchange under
     * the java help section.
     *
     * @param row
     *             rows input pram for array
     * @param col
     *             columns input pram for array
     * @param reverse
     *             boolean pram value determines whether col or rows
     *             is moved; in this rows case: (up or reverse down)
     * Uses recursion and dynamically shifts the board by rows uses
     * a placeholder I called isEdge to check where the edge is at.
     ******************************************************************/
    private void rowShift(int row, int col, boolean reverse) {
        // if we have same tiles we suck them in
        if (gameBoard[isEdge][col] == 0 || gameBoard[isEdge][col] == gameBoard[row][col]) {
            if (row > isEdge || (reverse && (isEdge > row))) {
                gameBoard[isEdge][col] += gameBoard[row][col];
                gameBoard[row][col] = 0;
            }
        } else {
            if (reverse) {
                isEdge--;
            } else {
                isEdge++;
            }
            // look around to shift away zeroes in sequences
            rowShift(row, col, reverse);
        }
    }
    /*******************************************************************
     * Credit for these two methods are given to stack exchange under
     * the java help section.
     *
     * @param row
     *             rows input pram for array
     * @param col
     *             columns input pram for array
     * @param reverse
     *             boolean pram value determines whether col or rows
     *             is moved in columns case :(left or reverse right)
     * Uses recursion and dynamically shifts the board by rows uses
     * a placeholder I called isEdge to check where the edge is at.
     ******************************************************************/
    private void colShift(int row, int col, boolean reverse) {
        // if we have same tiles we suck them in
        if (gameBoard[row][isEdge] == 0 || gameBoard[row][isEdge] == gameBoard[row][col]) {
            if (col > isEdge || (reverse && (isEdge > col))) {
                gameBoard[row][isEdge] += gameBoard[row][col];
                gameBoard[row][col] = 0;
            }
        } else {
            if (reverse) {
                isEdge--;
            } else {
                isEdge++;
            }
            // look around to shift away zeroes in sequences
            colShift(row, col, reverse);
        }
    }

    /*******************************************************************
     * Create an ArrayList of all tiles with values.
     *
     * @return an arrayList of Cells.
     ******************************************************************/
    @Override
    public ArrayList<Cell> getNonEmptyTiles() {
        Cell cell;
        ArrayList<Cell> cellArray = new ArrayList();

        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                if (gameBoard[i][j] != 0) {
                    cell = new Cell();
                    cell.row = i;
                    cell.column = j;
                    cell.value = gameBoard[i][j];
                    cellArray.add(cell);
                }
        return cellArray;
    }

    /*******************************************************************
     * Return the current game status
     *
     * @return one of the possible game states
     ******************************************************************/
    @Override
    public GameStatus getStatus() {
        // Cell c = new Cell();
        ArrayList<Cell> cellArray = getNonEmptyTiles();
        //Cell cell = cellArray.get(c.getValue());


        //code to define what is means by status  USER_WON,    /* the player is able to add the numbers to the goal value */
        for(Cell c : cellArray) {
            if (c.getValue() == WIN) {
                status = GameStatus.USER_WON;
                return status;
            }
        }
        //code rechecks status for valid move each time on gameBoard if passed zeros check then another important if board full with moves still left.
//        if (getNonEmptyTiles().size() == ROWS * COLS && (emptyFound = false)){
//            status = GameStatus.DIR_ERR;
//        }
        //code to define what is means by status IN_PROGRESS, /* game is still in progress */
        if(getNonEmptyTiles().size() == (ROWS * COLS))
            status = GameStatus.USER_LOST;
        else
            status = GameStatus.IN_PROGRESS;

        // check if its possible for the full board to move horizontally
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS - 1; col++)
                if (gameBoard[row][col] == gameBoard[row][col + 1])
                    status = GameStatus.IN_PROGRESS;

        // check if its possible for the full board to move vertically
        for (int col = 0; col < COLS; col++)
            for (int row = 0; row < ROWS - 1; row++)
                if (gameBoard[row][col] == gameBoard[row + 1][col])
                    status = GameStatus.IN_PROGRESS;

        return status;
    }

    /*******************************************************************
     * Undo the most recent action, i.e. restore the board to its previous
     * state.
     *
     * @throws IllegalStateException
     *             when undo is not possible
     ******************************************************************/
    @Override
    public void undo() {
        // throws an IllegalState if no more history step
        if (moves.size() < 1)
            throw new IllegalStateException("Cannot undo again!");

        // replace the current board
        gameBoard = moves.pop();
    }

}