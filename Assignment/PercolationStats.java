/******************************************************************************
 *  Compilation:  javac-algs4 PercolationStats.java
 *  Execution:    java-algs4 PercolationStats
 *  Dependencies: none
 *
 *  Estimates the value of the percolation threshold of n-by-n grid
 *  using= Monte Carlo Simulation
 *
 ******************************************************************************/
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.lang.IllegalArgumentException;

public class PercolationStats {
    // array containing percolation threshold for each trial
    private double[] p_thresholds;
    // number of trials to perform
    private int trials;
    //perform trials of independent experiments on an n-by-n grid sites
    public PercolationStats(int n, int trials) {
        if (n < 1 || trials < 1) throw new IllegalArgumentException();
        this.p_thresholds = new double[trials];
        this.trials = trials;
        // to generate random number in range [low, high)
        int low = 1;
        int high = n+1;
        // coordinates of site on the grid
        int x, y;
        // counter for open sites
        int site_counter = 0;
        // begin the trials
        for(int i=0; i<trials; i++) {
            Percolation trial = new Percolation(n);
            // keep opening random sites until there is percolation
            while(!trial.percolates()) {
                x = StdRandom.uniform(low, high);
                y = StdRandom.uniform(low, high);
                trial.open(x, y);
            }
            site_counter = trial.numberOfOpenSites();
            p_thresholds[i] = (double)site_counter / (n*n);
        }
    }
    // sample mean of percoaltion threshold
    public double mean() {
        return StdStats.mean(this.p_thresholds);
    }
    // sample standard deviation of percolation threshold
    public double stddev() {
        if(this.trials < 2) {
            return Double.NaN;
        }
        return StdStats.stddev(this.p_thresholds);
    }
    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - ( (1.96*stddev())/Math.sqrt(this.trials) );
    }
    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + ( (1.96*stddev())/Math.sqrt(this.trials) );
    }
    /*
     * test client that performs the monte-carlo experiment
     * that computes the sample mean, sample standard deviation, and the
     * 95% confidence interval for the percolation threshold
     *
     * @args[0]: size of grid
     * @args[1]: number of experiments to perform
     */
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        Stopwatch runningTime = new Stopwatch();
        PercolationStats experiment = new PercolationStats(n, trials);
        StdOut.printf("program took %f seconds\n", runningTime.elapsedTime());
        StdOut.printf("mean = %f\n", experiment.mean());
        StdOut.printf("stddev = %f\n", experiment.stddev());
        StdOut.printf("95%% confidence interval = %f, %f\n", experiment.confidenceLo(), experiment.confidenceHi());
    }
}
