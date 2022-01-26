public class Shape2D {
    private int sides;
    private String name;

    //constructor
    Shape2D(int newSides){
        if(newSides >= 0){
            sides = newSides;
            if(sides == 3){
                name = "triangle";
            }
        }else{
            System.out.println("that shape does not exit!");
        }
    }

    Shape2D(){
        sides = 0;
        name = "point";
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
}
