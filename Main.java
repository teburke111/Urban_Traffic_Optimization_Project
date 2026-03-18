import javax.swing.SwingUtilities;

public class Main {
    public static void main(String args[]){
        // Street street1 = new Street(10, 3, 0, 4, "Lesha Drive");
        // System.out.println(street1);


        Graph graph = new Graph();
        graph.closeStreet("Lesha Drive");

        SwingUtilities.invokeLater(() -> {
            new GraphFrame(graph).setVisible(true);
        });

    }
}
