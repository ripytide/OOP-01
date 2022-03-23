package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import javafx.beans.Observable;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.ArrayList;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {

	@Nonnull @Override public Model build(GameSetup setup,
	                                      Player mrX,
	                                      ImmutableList<Player> detectives) {
		// TODO
		throw new RuntimeException("Implement me!");
	}

	private class MyModel implements Model{
		ArrayList<Observer> observers;
		Board.GameState gameState;

		public MyModel(Board.GameState gameState){
			observers = new ArrayList<>();
			this.gameState = gameState;
		}

		@Nonnull
		@Override
		public Board getCurrentBoard() {
			return null;
		}

		@Override
		public void registerObserver(@Nonnull Observer observer) {
			observers.add(observer);
		}

		@Override
		public void unregisterObserver(@Nonnull Observer observer) {

		}

		@Nonnull
		@Override
		public ImmutableSet<Observer> getObservers() {
			return null;
		}

		@Override
		public void chooseMove(@Nonnull Move move) {

		}
	}
}
