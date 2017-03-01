/******************************************************************************
 *  Data Type that models a percolation system
 ******************************************************************************/
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.lang.IllegalArgumentException;

public class Percolation {
    private enum State {BLOCKED, OPEN}; // every site is either BLOCKED or OPEN
    private State[][] grid;
    private State virtual_top;      // single site that represents all sites in the top row
    private int virtual_top_id;
    private State virtual_bottom;   // single site that represents all sites in the bottom row
    private int virtual_bottom_id;
    private int size;               // dimension of the grid
    private int open_site_counter;  // number of open sites in the grid
    private boolean percolates;

    /*
     * disjoint-sets data type that models the connectivity of the sites
     * in the grid
     */
    private WeightedQuickUnionUF connections;

    /* create n-by-n grid that models the system*/
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        // initialize vars
        this.percolates = false;
        this.virtual_top = State.OPEN;
        this.virtual_top_id = n*n;
        this.virtual_bottom = State.OPEN;
        this.virtual_bottom_id = n*n+1;
        this.size = n;
        this.open_site_counter = 0;
        this.connections = new WeightedQuickUnionUF(n*n+2);
        this.grid = new State[n][n];
        // all sites initialized to BLOCKED
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.grid[i][j] = State.BLOCKED;
            }
        }
   }
   /*
    * opens site at (row, col) if it is not open already
    * @param row, col: [1, size]
    */
   public void open(int row, int col) {
       if (row < 1 || row > this.size || col < 1 || col > this.size) {
           throw new IndexOutOfBoundsException("open: row index i out of bounds");
       }
       if (this.grid[row-1][col-1] == State.OPEN) {
           return;
       }
       this.grid[row-1][col-1] = State.OPEN;
       this.open_site_counter++;
       /* connect to open neighbors*/
       // top neighbor
       if (row-1 > 0 && isOpen(row-1, col)) {
           connections.union( convert_D2_D1(row, col), convert_D2_D1(row-1, col));
       }
       // bottom neighbor
       if (row+1 <= this.size && isOpen(row+1, col)) {
           connections.union( convert_D2_D1(row, col),  convert_D2_D1(row+1, col));
       }
       // left neighbor
       if (col-1 > 0 && isOpen(row, col-1)) {
           connections.union( convert_D2_D1(row, col), convert_D2_D1(row, col-1) );
       }
       // right neighbor
       if (col+1 <= this.size && isOpen(row, col+1)) {
           connections.union( convert_D2_D1(row, col), convert_D2_D1(row, col+1) );
       }
       // connect to virtual sites if top/bottom site
       if(row==1) {
           connections.union( convert_D2_D1(row, col), this.virtual_top_id);
       }
       if(row==this.size && this.percolates == false) {
           connections.union( convert_D2_D1(row, col), this.virtual_bottom_id);
       }
   }

   /*
    * @return: true if site at (row, col) is open
    *          false otherwise
    */
   public boolean isOpen(int row, int col) {
       if (row < 1 || row > this.size || col < 1 || col > this.size) {
           throw new IndexOutOfBoundsException("isOpen: row index i out of bounds");
       }
       return this.grid[row-1][col-1]==State.OPEN;
   }

   /*
    * @return: true if site at (row, col) is full
    *          false otherwise
    * A site is full if there is a path to any site in the top row
    */
   public boolean isFull(int row, int col) {
       if (row < 1 || row > this.size || col < 1 || col > this.size) {
           throw new IndexOutOfBoundsException("isFull: row index i out of bounds");
       }
       // site is not full if it's BLOCKED
       if (!isOpen(row, col)) {
           return false;
       }
       if (row == 1) {
           return true;
       }
       // for each open site in the top row, is there a path to grid[row][col]
       if(connections.connected(convert_D2_D1(row, col), this.virtual_top_id) ) {
           return true;
       }
       return false;
   }

   /*
    * helper method that converts 2D coordinates (row, col) to 1D array index
    */
   private int convert_D2_D1(int row, int col) {
       return (this.size*(row-1)+col-1);
   }

   /*
    * @return: the number of open sites
    */
   public int numberOfOpenSites() {
       return this.open_site_counter;
   }

   /*
    * @return: true if the system percolates
    *          false otherwise
    * The system percolates if there is a path from any site in the bottom row
    * to any site in the top row
    */
   public boolean percolates() {
       /*
        * There is a path from the bottom row to the top row if
        * virtual bottom is in the same set as virtual top
        */
       if(connections.connected(this.virtual_top_id, this.virtual_bottom_id)) {
           this.percolates = true;
           return true;
       }
       return false;
   }
}
