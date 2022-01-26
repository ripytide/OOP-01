class HelloWorld {
  public static void main (String[] args) {
    //System.out.println("Hello World!");
    Shape2D square = new Shape2D(4);
    square.setName("square");

    Shape3D cube = new Shape3D(6, 12);
    cube.setName("cube");

    System.out.println("a "+square.getName()+" has "+square.getSides()+" sides");
    System.out.println("a "+cube.getName()+" has "+cube.getSides()+" edges and "+cube.getFaces()+" faces");

    System.out.println("the cube rolled a "+cube.roll());
  }
}