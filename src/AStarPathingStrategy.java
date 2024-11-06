
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point end, //step 1
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {


        HashMap<Point, Node> openList = new HashMap<>();
        HashMap<Point, Node> closedList = new HashMap<>();
        PriorityQueue<Node> plist= new PriorityQueue<>(Comparator.comparingInt(Node::getF));

        Node currentNode = new Node(null, start, end);

        openList.put(start, currentNode);
        plist.add(currentNode);
        Node current;

        while (!openList.isEmpty() && !withinReach.test(currentNode.getPoint(), end)) {
            List<Point> neighbors = potentialNeighbors.apply(currentNode.getPoint()).filter(canPassThrough).toList();
            for (int i = 0; i < neighbors.size(); i++) {

                if (!closedList.containsKey(neighbors.get(i))) {
                    current = new Node(currentNode, neighbors.get(i), end);
                    if (!openList.containsKey(neighbors.get(i))) {
                        openList.put(neighbors.get(i), current);
                        plist.add(current);
                    } else {
                        if (openList.get(neighbors.get(i)).getG() > current.getG()) {
                            openList.replace(neighbors.get(i), current);
                            plist.remove(neighbors.get(i));
                            plist.add(current);
                        }
                    }
                }
            }
            plist.remove(currentNode);
            closedList.put(currentNode.getPoint(), currentNode);
            openList.remove(currentNode.getPoint());
            currentNode = plist.peek();

        }
        List<Point> pointsPath = new ArrayList<>();
        Node p = currentNode;
        if (p != null) {
            while (p.getPrev() != null) {
                pointsPath.add(0, p.getPoint());
                p = p.getPrev();
            }
        }
        return pointsPath;
    }
}
class Node {
    private Point point;
    private Node prev;
    private int g;
    private int h;
    private int f;

    public Node(Node prev, Point point, Point end) {
        this.point = point;
        this.h = this.findH(end);
        if (prev != null) {
            this.g = prev.g + 1;
        }
        else {
            this.g = 0;
        }
        this.f = this.g + this.h;
        this.prev = prev;
    }

    public int findH(Point goal){
        int a = Math.abs(goal.y - this.point.y);
        int b = Math.abs(goal.x - this.point.x);
        return a + b;
    }
    public int getF() { return this.f; }
    public Point getPoint() { return this.point; }
    public int getG() { return this.g; }
    public Node getPrev() { return this.prev; }
}
