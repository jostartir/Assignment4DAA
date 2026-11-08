import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];

        if (n == 0) {
            return tested;
        }

        long[][] dp = new long[n][2];

        calculateCosts(0, -1, nb, cost, dp);

        tested[0] = dp[0][1] < dp[0][0];

        for (int i = 0; i < nb[0].length; i++) {
            int child = nb[0][i];

            rebuildTests(child, 0, tested[0], nb, dp, tested);

        }

        return tested;

    }

    private static void calculateCosts(int v, int parent, int[][] nb, int[] cost, long[][] dp) {

        dp[v][1] = cost[v];

        for (int j = 0; j < nb[v].length; j++) {
            int child = nb[v][j];

            if (child == parent) {
                continue;

            }

            calculateCosts(child, v, nb, cost, dp);

            dp[v][1] += Math.min(dp[child][0], dp[child][1]);

            dp[v][0] += dp[child][1];
        }
    }

    private static void rebuildTests(int v, int parent, boolean parentTested, int[][] nb, long[][] dp, boolean[] tested) {

        if (parentTested) {
            tested[v] = dp[v][1] < dp[v][0];

        } else {
            tested[v] = true;
        }

        for (int j = 0; j < nb[v].length; j++) {
            int child = nb[v][j];

            if (child != parent) {
                rebuildTests(child, v, tested[v], nb, dp, tested);

            }

        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) {
            return 0;
        }

        int farthest = 0;
        int[] distance = new int[n];
        Arrays.fill(distance, -1);

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(0);
        distance[0] = 0;

        while (!queue.isEmpty()) {
            int v = queue.remove();

            for (int j = 0; j < nb[v].length; j++) {

                int next = nb[v][j];

                if (distance[next] == -1) {
                    distance[next] = distance[v] + 1;
                    queue.add(next);

                    if (distance[next] > distance[farthest]) {
                        farthest = next;
                    }
                }
            }
        }

        Arrays.fill(distance, -1);
        queue.add(farthest);
        distance[farthest] = 0;
        int longest = 0;

        while (!queue.isEmpty()) {
            int v = queue.remove();

            for (int j = 0; j < nb[v].length; j++) {

                int next = nb[v][j];

                if (distance[next] == -1) {
                    distance[next] = distance[v] + 1;
                    longest = Math.max(longest, distance[next]);
                    queue.add(next);
                }
            }
        }

        return longest;
    }
}
