package uk.ac.bris.cs.scotlandyard.model;

public class GetEndLocationVisitor implements Move.Visitor<Integer> {

    @Override
    public Integer visit(Move.SingleMove move) {
        return move.destination;
    }

    @Override
    public Integer visit(Move.DoubleMove move) {
        return move.destination2;
    }
}
