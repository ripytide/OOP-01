import java.util.Random;

public class Shape3D extends Shape2D {
    private int faces;

    Shape3D(int newFaces, int newSides){
        super(newSides);
        if(newFaces >= 0){
            faces = newFaces;
        }
    }

    public int getFaces(){
        return faces;
    }

    public void setFaces(int newFaces){
        if(newFaces >= 0){
            faces = newFaces;
        }
    }

    public int roll(){
        Random rand = new Random();
        return (rand.nextInt(faces) + 1);
    }
}
