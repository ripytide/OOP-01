public class Shape {
    private int sides;
    private String name;
    private int dimension;

    //constructor
    Shape(int newSides, int newDimension){
        if(newSides > 2 && dimension >= 0){
            sides = newSides;
            dimension = newDimension;
            if(sides == 3){
                name = "triangle";
            }
        }
    }

    //getters and setters
    public void setName(String newName){
        name = newName;
    }

    public String getName(){
        return name;
    }

    public int getSides(){
        return sides;
    }

    public int getDimension(){
        return dimension;
    }
}
