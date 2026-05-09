import javax.swing.SwingUtilities;

public class Main {
    public static void main(String args[]){

        Graph graph = new Graph();

        SwingUtilities.invokeLater(() -> {
            new GraphFrame(graph).setVisible(true);
        });

    }
}
