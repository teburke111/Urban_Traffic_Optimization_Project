public class Street{

    //Fields
    private Node from;
    private Node to;
    private double distance;
    private int congestionLevel;
    private double tollCost;
    private double travelTime;
    private String name;
    private boolean open = true;
    private int direction;


    // Constructor
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

    // GETTERS AND SETTERS
    public double getTravelTime(){
        return travelTime;
    }

    public void setOpen(boolean set){
        open = set;
    }

    public void setCongestionLevel(int level){
        congestionLevel = level;
        travelTime = distance * congestionLevel;
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