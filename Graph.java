import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Graph {
    
    //Fields
    private Map<Node, List<Street>> trafficGraph;
    private boolean tollsAllowed;
    private boolean emergency;

    // Constructor
    public Graph(){
        tollsAllowed = true;
        emergency = false;
        trafficGraph = new HashMap<>();

        // Create nodes to the graph
        Node node1 = new Node(250, 50);
        Node node2 = new Node(450, 50);
        Node node3 = new Node(650, 50);
        Node node4 = new Node(850, 50);

        Node node5 = new Node(250, 250);
        Node node6 = new Node(50, 250);
        Node node7 = new Node(450, 250);
        Node node8 = new Node(650, 250);
        Node node9 = new Node(850, 250);

        Node node10 = new Node(50, 450);
        Node node11 = new Node(250, 450);
        Node node12 = new Node(450, 450);
        Node node13 = new Node(650, 450);
        Node node14 = new Node(850, 450);

        Node node15 = new Node(250, 650);
        Node node16 = new Node(450, 650);
        Node node17 = new Node(650, 650);
        Node node18 = new Node(850, 650);


        // Connect nodes with created streets
        Street street1 = new Street(node1, node5, 2.5, 1, 0, 5, "Maple Ave");
        Street street2 = new Street(node2, node3, 3.2, 1, 0, 0, "Oak Street");
        Street street3 = new Street(node3, node4, 4.0, 1, 0, 0, "Pine Road");
        Street street4 = new Street(node2, node7, 2.8, 1, 0, 2, "Cedar Lane");
        Street street5 = new Street(node3, node8, 3.5, 1, 0, 3, "Birch Blvd");
        Street street6 = new Street(node4, node9, 4.2, 1, 0, 0, "Elm Street");
        Street street7 = new Street(node5, node6, 2.0, 1, 0, 0, "Spruce Way");
        Street street8 = new Street(node5, node7, 3.1, 1, 0, 2, "Willow Drive");
        Street street9 = new Street(node7, node8, 3.7, 1, 0, 0, "Cherry Lane");
        Street street10 = new Street(node8, node9, 4.5, 1, 0, 0, "Poplar Street");
        Street street11 = new Street(node5, node10, 2.3, 1, 0, 2, "Ash Court");
        Street street12 = new Street(node5, node11, 2.9, 1, 0, 0, "Hickory Ave");
        Street street13 = new Street(node11, node12, 3.6, 1, 0, 0, "Walnut Street");
        Street street14 = new Street(node7, node12, 3.3, 1, 0, 3, "Chestnut Blvd");
        Street street15 = new Street(node12, node13, 4.1, 1, 0, 0, "Sycamore Road");
        Street street16 = new Street(node13, node14, 4.8, 1, 0, 6, "Redwood Drive");
        Street street17 = new Street(node11, node15, 2.7, 1, 0, 0, "Dogwood Lane");
        Street street18 = new Street(node15, node16, 3.4, 1, 0, 0, "Magnolia Ave");
        Street street19 = new Street(node12, node16, 3.9, 1, 0, 0, "Aspen Street");
        Street street20 = new Street(node18, node17, 5.0, 1, 0, 0, "Sequoia Blvd");
        Street street21 = new Street(node14, node18, 1.4, 1, 0, 0, "Lesha Drive");


        List<Node> nodes = new ArrayList<>(Arrays.asList(
            node1, node2, node3, node4, node5, node6,
            node7, node8, node9, node10, node11, node12,
            node13, node14, node15, node16, node17, node18
        ));

        List<Street> streets = new ArrayList<>(Arrays.asList(
            street1, street2, street3, street4, street5,
            street6, street7, street8, street9, street10,
            street11, street12, street13, street14, street15,
            street16, street17, street18, street19, street20, street21
        ));

        // add nodes to graph
        for (Node node : nodes){
            addNode(node);
        }

        // add streets to graph, as well as its other side
        for (Street street : streets){
            trafficGraph.get(street.getFrom()).add(street);

            if (street.getDirectionVal() == 0) {
                Street reverse = new Street(
                    street.getTo(),
                    street.getFrom(),
                    street.getTravelTime(),   
                    1,                       
                    0,                       
                    0,                        
                    street.toString()       
                );

                trafficGraph.get(street.getTo()).add(reverse);
            }
        }

                
    }

    // Return graph
    public Map<Node, List<Street>> getGraph() {
        return trafficGraph;
    }

    // Calcluate shortest street path
    public List<Street> shortestPath(int s, int e, double alpha) {
        Node start =  null; 
        Node end = null;

        for (Node node : trafficGraph.keySet()){
            if (node.getId() == s){
                start = node;
            }

            if (node.getId() == e){
                end = node;
            }
        }

        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Street> prevStreet = new HashMap<>();

        PriorityQueue<Map.Entry<Node, Double>> pq =
            new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        for (Node node : trafficGraph.keySet()) {
            dist.put(node, Double.MAX_VALUE);
        }

        dist.put(start, 0.0);
        pq.add(new AbstractMap.SimpleEntry<>(start, 0.0));

        while (!pq.isEmpty()) {
            Map.Entry<Node, Double> currentEntry = pq.poll();
            Node current = currentEntry.getKey();
            double currentDist = currentEntry.getValue();

            if (currentDist > dist.get(current)) continue;

            if (current.equals(end)) break;

            for (Street street : trafficGraph.get(current)) {

                if (street.gettollCost() != 0 && tollsAllowed == false) {
                    continue; 
                }
                
                if (!street.isOpen()){
                    if (!emergency) {
                        continue;
                    }

                    Node from = street.getFrom();
                    Node to = street.getTo();

                    boolean reverseOpen = false;

                    for (Street reverse : trafficGraph.get(to)) {
                        if (reverse.getTo().equals(from) && reverse.isOpen()) {
                            reverseOpen = true;
                            break;
                        }
                    }

                    if (!reverseOpen) {
                        continue; 
                    }
                }

                Node neighbor = street.getTo();
                // double newDist = dist.get(current) + street.getTravelTime();

                double time = street.getTravelTime();
                double cost = street.gettollCost();

                double newDist = dist.get(current) + (alpha * time) + ((1 - alpha) * cost);

                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    prevStreet.put(neighbor, street);
                    pq.add(new AbstractMap.SimpleEntry<>(neighbor, newDist));
                }
            }
        }

        List<Street> path = new ArrayList<>();
        Node current = end;

        if (!prevStreet.containsKey(end)) {
            return path;
        }

        while (!current.equals(start)) {
            Street street = prevStreet.get(current);
            path.add(street);
            current = street.getFrom();
        }

        Collections.reverse(path);
        return path;
    }

    // Add node method 
    public void addNode(Node node, List<Street> streets){
        trafficGraph.put(node, streets);
    }

    // Change congestion level based on time from GUI
    public void setCongestionLevel(int level){ // level should be 1-3

        for (Node node : trafficGraph.keySet()) {
            List<Street> streets = trafficGraph.get(node);

            for (Street street : streets){
                street.setCongestionLevel(level);
            }
        }
    }
    

    // Add Node method
    public void addNode(Node node){
        trafficGraph.put(node, new ArrayList<Street>());
    }

    // Close Street from GUI
    public void closeStreet(String streetName) {

        for (Node node : trafficGraph.keySet()) {
            List<Street> streets = trafficGraph.get(node);

            for (Street street : streets){
                if (street.getName().equals(streetName)){
                    street.setOpen(false);
                }
            }
        }
    }

    // Open a street from GUI
    public void openStreet(String streetName) {

        for (Node node : trafficGraph.keySet()) {
            List<Street> streets = trafficGraph.get(node);

            for (Street street : streets){
                if (street.getName().equals(streetName)){
                    street.setOpen(true);
                }
            }
        }
    }

    // Checks if a certain street is open
    public boolean isOpen(String streetName) {

        for (Node node : trafficGraph.keySet()) {
            List<Street> streets = trafficGraph.get(node);

            for (Street street : streets){
                if (street.getName().equals(streetName)){
                    return street.isOpen();
                }
            }
        }

        return true;
    }

    // Make the street a oneway or change direction of street
    public void changeStreetDirection(String streetName, boolean both) {
    
        List<Street> matchingStreets = new ArrayList<>();
        
        for (Node node : trafficGraph.keySet()) {
            for (Street street : trafficGraph.get(node)) {
                if (street.getName().equals(streetName)) {
                    matchingStreets.add(street);
                }
            }
        }

        if (both) {

            for (Street street : matchingStreets) {
                street.setOpen(true);
            }

        } else {

            if (matchingStreets.size() == 2) {
                Street dir1 = matchingStreets.get(0);
                Street dir2 = matchingStreets.get(1);

                if (dir1.isOpen() && !dir2.isOpen()) {
                    dir1.setOpen(false);
                    dir2.setOpen(true);
                } else if (!dir1.isOpen() && dir2.isOpen()) {
                    dir1.setOpen(true);
                    dir2.setOpen(false);
                } else {
                    dir1.setOpen(true);
                    dir2.setOpen(false);
                }
            } 
        }
    }

    // SETTERS AND GETTERS
    
    public void setTolls(boolean b){
        tollsAllowed = b;
    }

    public void setEmergency(boolean b){
        emergency = b;
    }

    public boolean getEmergency(){
        return emergency;
    }

    
}
