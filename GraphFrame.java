import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GraphFrame extends JFrame {

    private Graph graphObj;
    private Map<Node, List<Street>> graph;
    private int i = 0;
    private int startNode;
    private int endNode;

    private JTextArea outputTextArea;

    public GraphFrame(Graph graph) {

        graphObj = graph;

        this.graph = graph.getGraph();

        setTitle("Traffic Graph");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new GraphPanel());

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setMargin(new Insets(10, 10, 10, 10)); // Add some padding
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setPreferredSize(new Dimension(250, 800)); // Set the width of the right panel
        scrollPane.setBorder(BorderFactory.createTitledBorder("Directions")); // Give it a nice title
        
        add(scrollPane, BorderLayout.EAST);
    }

    class GraphPanel extends JPanel {

        private final int NODE_RADIUS = 10;

        public GraphPanel() {
            // Add a mouse listener to detect clicks on the canvas
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    for (Node node : graph.keySet()) {
                        double distance = Math.sqrt(Math.pow(mouseX - node.getX(), 2) + Math.pow(mouseY - node.getY(), 2));
                        
                        if (distance <= NODE_RADIUS) {
                            i++;

                            if (i == 1){
                                System.out.println("Start Node: " + node.getId());
                                startNode = node.getId();
                                endNode = -1; 

                                outputTextArea.setText("");
                            } else {
                                System.out.println("End Node: " + node.getId());
                                endNode = node.getId();

                                List<Street> lst = graphObj.shortestPath(startNode, endNode);


                                if (!lst.isEmpty()){
                                    outputTextArea.append("------CALCULATED ROUTE------\n");
                                    double totalTime = 0;
                                    double tolls = 0;

                                    for (Street street : lst){
                                        outputTextArea.append(street.getName() + " - " + street.getTravelTime() + "\n");
                                        totalTime += street.getTravelTime();
                                        tolls += street.gettollCost();
                                    }

                                    outputTextArea.append("Total Travel Time: " + totalTime + "\n");
                                    outputTextArea.append("Total Tolls: " + tolls + "\n");
                                }else{
                                    outputTextArea.append("There is no possible route\n");

                                }
                                i = 0;
                            }
                                
                            // IMPORTANT: Tell the panel to refresh its graphics!
                            repaint(); 
                            break; 
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setBackground(new Color(60, 179, 113)); 

            int roadWidth = 30;

            // --- LAYER 1: THE ASPHALT ---
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (Node node : graph.keySet()) {
                for (Street street : graph.get(node)) {
                    Node to = street.getTo();
                    g2d.drawLine(node.getX(), node.getY(), to.getX(), to.getY());
                }
            }

            // --- LAYER 2: THE INTERSECTIONS (Now with color logic!) ---
            for (Node node : graph.keySet()) {
                
                // 1. CHANGE SWARE COLOR BASED ON SELECTION
                if (node.getId() == startNode) {
                    g2d.setColor(Color.GREEN); // Start intersection is Cyan
                } else if (node.getId() == endNode) {
                    g2d.setColor(Color.RED);  // End intersection is Red
                } else {
                    g2d.setColor(Color.GRAY); // Default intersection is Gray
                }

                int offset = roadWidth / 2;
                g2d.fillRoundRect(node.getX() - offset, node.getY() - offset, roadWidth, roadWidth, 10, 10);
            }

            // --- LAYER 3: DASHED CENTER LINES ---
            g2d.setColor(Color.YELLOW);
            float[] dashPattern = { 25.0f, 20.0f };
            Stroke openStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
            Stroke closedStroke = new BasicStroke(6.0f); // Thicker, solid line for closures
            
            java.util.Set<String> drawnStreets = new java.util.HashSet<>();
            for (Node node : graph.keySet()) {
                for (Street street : graph.get(node)) {
                    Node to = street.getTo();
                    int minId = Math.min(node.getId(), to.getId());
                    int maxId = Math.max(node.getId(), to.getId());
                    String roadId = minId + "-" + maxId;

                    if (!drawnStreets.contains(roadId)) {

                        if (!street.isOpen()) { 
                            g2d.setColor(Color.RED);     // Closed streets are Red
                            g2d.setStroke(closedStroke); // Solid line
                        } else {
                            g2d.setColor(Color.YELLOW);  // Open streets are Yellow
                            g2d.setStroke(openStroke);   // Dashed line
                        }

                        g2d.drawLine(node.getX(), node.getY(), to.getX(), to.getY());
                        drawnStreets.add(roadId);
                    }
                }
            }

            // --- LAYER 4: TEXT, LABELS AND UNIFORM CIRCLES ---
            g2d.setStroke(new BasicStroke(1.0f)); 
            
            int nodeDiameter = 20;
            int offset = nodeDiameter / 2;

            for (Node node : graph.keySet()) {
                // Draw street names
                for (Street street : graph.get(node)) {
                    Node to = street.getTo();
                    int midX = (node.getX() + to.getX()) / 2;
                    int midY = (node.getY() + to.getY()) / 2;
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(street.getName() + " " + String.format("%.2f", street.getTravelTime()), midX + 1, midY + 1);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(street.getName() + " " + String.format("%.2f", street.getTravelTime()), midX, midY);
                }
            }
        }
    }
}
