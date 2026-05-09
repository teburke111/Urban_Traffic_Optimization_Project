import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GraphFrame extends JFrame {

    //Fields
    private Graph graphObj;
    private Map<Node, List<Street>> graph;
    private int i = 0;
    private int startNode;
    private int endNode;
    private double alpha;
    private JTextArea outputTextArea;
    private List<Street> currentRoute;

    // Constructor 
    public GraphFrame(Graph graph) {

        graphObj = graph;

        alpha = 1.0;

        this.graph = graph.getGraph();

        setTitle("Traffic Graph");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new GraphPanel());

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setMargin(new Insets(10, 10, 10, 10)); 
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setPreferredSize(new Dimension(250, 800));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Directions"));
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        


        // RADIO BUTTONS
        JRadioButton avoidTolls = new JRadioButton("Avoid Tolls");
        JRadioButton emergency = new JRadioButton("Emergency Vehicle");
        JRadioButton balance = new JRadioButton("Balance Time & Cost");
        JRadioButton minTime = new JRadioButton("Minimize Travel Time");

        ButtonGroup group = new ButtonGroup();
        group.add(avoidTolls);
        group.add(emergency);
        group.add(balance);
        group.add(minTime);

        minTime.setSelected(true);

        controlsPanel.add(avoidTolls);
        controlsPanel.add(emergency);
        controlsPanel.add(balance);
        controlsPanel.add(minTime);

        avoidTolls.setAlignmentX(Component.LEFT_ALIGNMENT);
        emergency.setAlignmentX(Component.LEFT_ALIGNMENT);
        balance.setAlignmentX(Component.LEFT_ALIGNMENT);
        minTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        // CLOSE ROAD INPUT
        controlsPanel.add(Box.createVerticalStrut(10));
        JLabel closeRoadLabel = new JLabel("Close/Open Road:");
        JTextField closeRoadField = new JTextField(15);

        controlsPanel.add(closeRoadLabel);
        controlsPanel.add(closeRoadField);

        // CHANGE STREET DIRECTION
        controlsPanel.add(Box.createVerticalStrut(10));
        JLabel changeDirectionLabel = new JLabel("Change Road Direction:");
        JTextField changeDirectionField = new JTextField(15);

        controlsPanel.add(changeDirectionLabel);
        controlsPanel.add(changeDirectionField);

        // TIME OF DAY DROPDOWN
        controlsPanel.add(Box.createVerticalStrut(10));
        JLabel timeLabel = new JLabel("Time of Day:");

        String[] timesOfDay = {
                "1 AM",
                "2 AM",
                "3 AM",
                "4 AM",
                "5 AM",
                "6 AM",
                "7 AM",
                "8 AM",
                "9 AM",
                "10 AM",
                "11 AM",
                "12 PM",
                "1 PM",
                "2 PM",
                "3 PM",
                "4 PM",
                "5 PM",
                "6 PM",
                "7 PM",
                "8 PM",
                "9 PM",
                "10 PM",
                "11 PM",
                "12 AM",

        };

        JComboBox<String> timeDropdown = new JComboBox<>(timesOfDay);

        controlsPanel.add(timeLabel);
        controlsPanel.add(timeDropdown);

        // APPLY BUTTON
        controlsPanel.add(Box.createVerticalStrut(10));
        JButton applyButton = new JButton("Apply");
        controlsPanel.add(applyButton);

        // APPLY BUTTON LOGIC
        applyButton.addActionListener(e -> {

            if (avoidTolls.isSelected()) {
                graphObj.setTolls(false);
                graphObj.setEmergency(false);
            } else if (emergency.isSelected()) {
                graphObj.setTolls(true);
                graphObj.setEmergency(true);
            } else if (minTime.isSelected()){
                graphObj.setEmergency(false);
                graphObj.setTolls(true);
                alpha = 1.0;
            }else {
                graphObj.setTolls(true);
                graphObj.setEmergency(false);
                alpha = 0.5;
            }

            String roadToClose = closeRoadField.getText();
            if (!roadToClose.isEmpty()) {
                if (graphObj.isOpen(roadToClose)){
                    graphObj.closeStreet(roadToClose);
                }else{
                    graphObj.openStreet(roadToClose);
                }
            }

            String roadToChangeDirection = changeDirectionField.getText();
            if (!roadToChangeDirection.isEmpty()) {
                graphObj.changeStreetDirection(roadToChangeDirection,false);
            }

            String selectedTime = (String) timeDropdown.getSelectedItem();
            if (selectedTime.equals("8 AM") || 
                selectedTime.equals("9 AM") || 
                selectedTime.equals("10 AM") ||
                selectedTime.equals("4 PM") ||
                selectedTime.equals("5 PM") ||
                selectedTime.equals("6 PM")) {

                graphObj.setCongestionLevel(3);
            }else if (selectedTime.equals("11 AM") || 
                selectedTime.equals("12 PM") || 
                selectedTime.equals("1 PM") || 
                selectedTime.equals("2 PM") || 
                selectedTime.equals("3 PM")) {

                graphObj.setCongestionLevel(2);
            }else{
                graphObj.setCongestionLevel(1);
            }

            changeDirectionField.setText("");
            closeRoadField.setText("");

            repaint();
        });

        // RIGHT PANEL
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(controlsPanel, BorderLayout.SOUTH);

        rightPanel.setPreferredSize(new Dimension(300, 800));

        add(rightPanel, BorderLayout.EAST);

    }

    class GraphPanel extends JPanel {

        private final int NODE_RADIUS = 10;

        public GraphPanel() {
            // detects mouse clicks
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
                                currentRoute = null;

                                outputTextArea.setText("");
                            } else {
                                System.out.println("End Node: " + node.getId());
                                endNode = node.getId();

                                currentRoute = graphObj.shortestPath(startNode, endNode, alpha);
                                List<Street> lst = currentRoute;


                                if (!lst.isEmpty()){
                                    outputTextArea.append("------CALCULATED ROUTE------\n");
                                    double totalTime = 0;
                                    double tolls = 0;

                                    for (Street street : lst){
                                        outputTextArea.append(street.getName() + " - " + String.format("%.2f", street.getTravelTime()) + "\n");
                                        totalTime += street.getTravelTime();
                                        tolls += street.gettollCost();
                                    }

                                    outputTextArea.append("Total Travel Time: " + String.format("%.2f", totalTime) + "\n");
                                    if (graphObj.getEmergency()){
                                        outputTextArea.append("Emergency Vehicles have no tolls.\n");
                                    }else{
                                        outputTextArea.append("Total Tolls: " + String.format("%.2f", tolls) + "\n");
                                    }
                                }else{
                                    outputTextArea.append("There is no possible route\n");

                                }
                                i = 0;
                            }
                                
                            repaint(); //refresh
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

            // ASPHALT
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (Node node : graph.keySet()) {
                for (Street street : graph.get(node)) {
                    Node to = street.getTo();
                    g2d.drawLine(node.getX(), node.getY(), to.getX(), to.getY());
                }
            }

            // INTERSECTIONS
            for (Node node : graph.keySet()) {
                
                if (node.getId() == startNode) {
                    g2d.setColor(Color.GREEN); 
                } else if (node.getId() == endNode) {
                    g2d.setColor(Color.RED);  
                } else {
                    g2d.setColor(Color.GRAY); 
                }

                int offset = roadWidth / 2;
                g2d.fillRoundRect(node.getX() - offset, node.getY() - offset, roadWidth, roadWidth, 10, 10);
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 10));


            // DASHED CENTER LINES & ARROWS (one way)
            float[] dashPattern = { 25.0f, 20.0f };
            Stroke openStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
            Stroke closedStroke = new BasicStroke(6.0f); 
            
            java.util.Set<String> drawnStreets = new java.util.HashSet<>();
            for (Node node : graph.keySet()) {
                for (Street street : graph.get(node)) {
                    Node to = street.getTo();
                    
                    int minId = Math.min(node.getId(), to.getId());
                    int maxId = Math.max(node.getId(), to.getId());
                    String roadId = minId + "-" + maxId;

                    if (!drawnStreets.contains(roadId)) {
                        
                        boolean forwardOpen = street.isOpen();
                        boolean backwardOpen = false;
                        
                        for (Street reverseStreet : graph.get(to)) {
                            if (reverseStreet.getTo().getId() == node.getId()) {
                                backwardOpen = reverseStreet.isOpen();
                                break;
                            }
                        }

                        if (!forwardOpen && !backwardOpen) {
                            g2d.setColor(Color.RED);     
                            g2d.setStroke(closedStroke); 
                            g2d.drawLine(node.getX(), node.getY(), to.getX(), to.getY());
                            
                        } else if (forwardOpen && backwardOpen) {
                            g2d.setColor(Color.YELLOW);  
                            g2d.setStroke(openStroke);   
                            g2d.drawLine(node.getX(), node.getY(), to.getX(), to.getY());
                            
                        } else {
                            g2d.setColor(Color.YELLOW); 
                            g2d.setStroke(openStroke);
                            g2d.drawLine(node.getX(), node.getY(), to.getX(), to.getY());
                            
                            int startX = forwardOpen ? node.getX() : to.getX();
                            int startY = forwardOpen ? node.getY() : to.getY();
                            int endX = forwardOpen ? to.getX() : node.getX();
                            int endY = forwardOpen ? to.getY() : node.getY();
                            
                            g2d.setColor(Color.RED); 
                            drawArrowHead(g2d, startX, startY, endX, endY);
                        }

                        drawnStreets.add(roadId);
                    }
                }
            }

            // TEXT, LABELS AND CIRCLES
            g2d.setStroke(new BasicStroke(1.0f)); 
            

            for (Node node : graph.keySet()) {
                
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

            // 🔹 HIGHLIGHT ROUTE IN BLUE
            if (currentRoute != null) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                for (Street street : currentRoute) {
                    Node from = street.getFrom();
                    Node to = street.getTo();

                    g2d.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
                }
            }

            // INTERSECTIONS LABELS
            for (Node node : graph.keySet()) {

                g2d.setFont(new Font("Arial", Font.BOLD, 24));

                int x = node.getX();
                int y = node.getY();

                String label = String.valueOf(node.getId());

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(label);
                int textHeight = fm.getAscent();

                int textX = x - textWidth / 2;
                int textY = y + textHeight / 2 - 2;

                g2d.setColor(Color.BLACK);
                g2d.drawString(label, textX - 1, textY);
                g2d.drawString(label, textX + 1, textY);
                g2d.drawString(label, textX, textY - 1);
                g2d.drawString(label, textX, textY + 1);

                g2d.setColor(Color.WHITE);
                g2d.drawString(label, textX, textY);
            }
        }
    }

    private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 15; 

        int x3 = (int) (midX - arrowSize * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (midY - arrowSize * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (midX - arrowSize * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (midY - arrowSize * Math.sin(angle + Math.PI / 6));

        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3.0f)); 
        
        g2d.drawLine(midX, midY, x3, y3);
        g2d.drawLine(midX, midY, x4, y4);
        
        g2d.setStroke(oldStroke);
    }
}
