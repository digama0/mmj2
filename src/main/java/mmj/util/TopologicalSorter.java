package mmj.util;

import java.util.*;

/**
 * Sorts a directed graph, obtaining a visiting sequence ("sorted" list) that
 * respects the "Predecessors" (as in a job/task requirements list). (when there
 * is freedom, the original ordering is preferred) The behaviour in case of
 * loops (cycles) depends on the "mode": permitLoops == false : loops are
 * detected, but result is UNDEFINED (simpler) permitLoops == true : loops are
 * detected, result a "best effort" try, original ordering is privileged
 * http://en.wikipedia.org/wiki/Topological_sort
 *
 * @param <T> The node type
 * @see <a href="http://stackoverflow.com/a/2739636/890016">Sample Directed
 *      Graph and Topological Sort Code</a>
 */
public class TopologicalSorter<T> {

    private final boolean permitLoops;
    private final DirectedGrapher<T> grapher;
    private final Collection<T> graph; // original graph. this is not touched.
    private final List<T> sorted = new ArrayList<>(); // result
    private final Set<T> visited = new HashSet<>(); // auxiliar list
    private final Set<T> withLoops = new HashSet<>();

    // auxiliary: all successors (also remote) of each node; this is only used
    // if permitLoops==true
    private HashMap<T, Set<T>> succesors = null;

    public TopologicalSorter(final Collection<T> graph,
        final DirectedGrapher<T> grapher, final boolean permitLoops)
    {
        this.graph = graph;
        this.grapher = grapher;
        this.permitLoops = permitLoops;
    }

    public void sort() {
        init();
        for (final T n : graph)
            if (permitLoops)
                visitLoopsPermitted(n);
            else
                visitLoopsNoPermitted(n, new HashSet<T>());
    }

    private void init() {
        sorted.clear();
        visited.clear();
        withLoops.clear();
        // build successors map: only it permitLoops == true
        if (permitLoops) {
            succesors = new HashMap<>();
            final HashMap<T, Set<T>> addTo = new HashMap<>();
            for (final T n : graph) {
                succesors.put(n, new HashSet<T>());
                addTo.put(n, new HashSet<T>());
            }
            for (final T n2 : graph)
                for (final T n1 : grapher.getPredecessors(n2))
                    succesors.get(n1).add(n2);
            boolean change = false;
            do {
                change = false;
                for (final T n : graph) {
                    addTo.get(n).clear();
                    for (final T ns : succesors.get(n))
                        for (final T ns2 : succesors.get(ns))
                            if (!succesors.get(n).contains(ns2)) {
                                change = true;
                                addTo.get(n).add(ns2);
                            }
                }
                for (final T n : graph)
                    succesors.get(n).addAll(addTo.get(n));
            } while (change);
        }
    }

    private void visitLoopsNoPermitted(final T n,
        final Set<T> visitedInThisCallStack)
    { // this is simpler than visitLoopsPermitted
        if (visited.contains(n)) {
            if (visitedInThisCallStack.contains(n))
                withLoops.add(n); // loop!
            return;
        }
        // System.out.println("visiting " + n.toString());
        visited.add(n);
        visitedInThisCallStack.add(n);
        for (final T n1 : grapher.getPredecessors(n))
            visitLoopsNoPermitted(n1, visitedInThisCallStack);
        sorted.add(n);
    }

    private void visitLoopsPermitted(final T n) {
        if (visited.contains(n))
            return;
        // System.out.println("visiting " + n.toString());
        visited.add(n);
        for (final T n1 : grapher.getPredecessors(n)) {
            if (succesors.get(n).contains(n1)) {
                withLoops.add(n);
                withLoops.add(n1);
                continue;
            } // loop!
            visitLoopsPermitted(n1);
        }
        sorted.add(n);
    }

    public boolean hadLoops() {
        return withLoops.size() > 0;
    }

    public List<T> getSorted() {
        return sorted;
    }

    public Set<T> getWithLoops() {
        return withLoops;
    }

    public void showResult() { // for debugging
        for (final T node : sorted)
            System.out.println(node.toString());
        if (hadLoops()) {
            System.out.println("LOOPS!:");
            for (final T node : withLoops)
                System.out.println("  " + node.toString());
        }
    }

    @FunctionalInterface
    public interface DirectedGrapher<T> {
        public Collection<T> getPredecessors(T node);
    }
}
