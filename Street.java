public class Street{

    private Node from;
    private Node to;
    private double distance;
    private int congestionLevel;
    private double tollCost;
    private double travelTime;
    private String name;
    private boolean open = true;

    // 0 = bidirectional
    // 1 = right only
    // -1 = left only
    //     private int direction;
    private int direction;


    public Street(Node from, Node to, double distance, int congestionLevel, int direction, double tollCost, String name){

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.congestionLevel = congestionLevel;
        this.direction = direction;
        this.tollCost = tollCost;
        this.name = name;

        this.travelTime = distance * congestionLevel;

    }

    public double getTravelTime(){
        return travelTime;
    }

    public void setOpen(){
        open = !open;
    }

    public boolean isOpen(){
        return open;
    }

    public Node getTo(){
        return to;
    }

    public Node getFrom(){
        return from;
    }

    public int getDirectionVal(){
        return direction;
    }

    public String getDirection(){
        if (direction == 0){
            return "Two-Way";
        }else if (direction == -1){
            return "Left-Only";
        }else{
            return "Right-Only";
        }
    }

    // public String toString(){
    //     return "Name: " + name +
    //             "\nTime: " + travelTime +
    //             "\nDirection: " + getDirection() +
    //             "\nFrom: " + from +
    //             "\nTo: " + to; 
    // }

    public String toString(){
        return name;
    }

    public String getName(){
        return name;
    }

    public double gettollCost(){
        return tollCost;
    }

}