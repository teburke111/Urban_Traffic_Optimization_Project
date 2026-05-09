public class Node {

    //Fields
    private int id;
    private static int num = 1;

    // Used for GUI
    private int x;
    private int y;

    //Constructor
    public Node (int x, int y){
        this.id = num++;
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "Node: " + id;
    }

    // GETTERS
    public int getId(){
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
