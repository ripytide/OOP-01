class HelloWorld {
  public static void main (String[] args) {
    //System.out.println("Hello World!");
    Shape square = new Shape(4, 2);
    square.setName("square");

    System.out.println("a "+square.getName()+" has "+square.getSides()+" sides");
  }
}