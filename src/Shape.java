public class Shape {
    private int sides;
    private String name;
    private int dimension;

    //constructor
    Shape(int newSides, int newDimension){
        if((newDimension <= newSides) && (newDimension >= 0)){
            sides = newSides;
            dimension = newDimension;
            if(sides == 3){
                name = "triangle";
            }
        }else{
            System.out.println("that shape does not exit!");
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
