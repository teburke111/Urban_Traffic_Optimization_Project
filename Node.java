public class Node {

    private int id;
    private static int num = 1;

    private int x;
    private int y;


    public Node (int x, int y){
        this.id = num++;
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "Node: " + id;
    }

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
