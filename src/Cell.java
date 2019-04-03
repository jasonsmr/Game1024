/**
 * Created by Hans Dulimarta (Summer 2014)
 */
public class Cell implements Comparable<Cell> {
    public int row, column, value;

    public Cell()
    {
        this(0,0,0);
    }
    public Cell (int r, int c, int v)
    {
        row = r;
        column = c;
        value = v;
    }
    /***********************************************
     @return the row position
     ***********************************************/
    public int getRow(){
        return row;
    }

    /***********************************************
     @return the column position
     ***********************************************/
    public int getCol(){
        return column;
    }

    /***********************************************
     @return the numerical value
     ***********************************************/
    public int getValue(){
        return value;
    }
    /* must override equals to ensure List::contains() works
     * correctly
     */
    @Override
    public int compareTo (Cell other) {
        if (this.row < other.row) return -1;
        if (this.row > other.row) return +1;

        /* break the tie using column */
        if (this.column < other.column) return -1;
        if (this.column > other.column) return +1;

        return this.value - other.value;
    }
}
