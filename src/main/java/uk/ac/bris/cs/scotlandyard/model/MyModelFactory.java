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

		MyGameStateFactory myGameStateFactory = new MyGameStateFactory();
		Board.GameState newGameState = myGameStateFactory.build(setup, mrX, detectives);
		return new MyModel(newGameState);
	}

	private class MyModel implements Model {
		ArrayList<Observer> observers;
		Board.GameState gameState;

		public MyModel(Board.GameState gameState) {
			observers = new ArrayList<>();
			this.gameState = gameState;
		}

		@Nonnull
		@Override
		public Board getCurrentBoard() {
			return gameState;
		}

		@Override
		public void registerObserver(Observer observer) {
			if (observer == null) throw new NullPointerException("observer is null");
			if (observers.contains(observer)) throw new IllegalArgumentException("repeat observer");

			observers.add(observer);
		}

		@Override
		public void unregisterObserver(Observer observer) {
			if (observer == null) throw new NullPointerException("observer is null");
			if (!observers.contains(observer)) throw new IllegalArgumentException("observer not registered");

			observers.remove(observer);
		}

		@Nonnull
		@Override
		public ImmutableSet<Observer> getObservers() {
			return ImmutableSet.copyOf(observers);
		}

		@Override
		public void chooseMove(@Nonnull Move move) {
			gameState = gameState.advance(move);
			if (gameState.getWinner().isEmpty()) {
				for (Observer o : observers) {
					o.onModelChanged(gameState, Observer.Event.MOVE_MADE);
				}
			} else {
				for (Observer o : observers) {
					o.onModelChanged(gameState, Observer.Event.GAME_OVER);
				}
			}
		}
	}
}
