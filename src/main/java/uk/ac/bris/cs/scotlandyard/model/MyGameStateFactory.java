package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(GameSetup setup,
											  Player mrX,
											  ImmutableList<Player> detectives){
		if(mrX == null ){
			throw new NullPointerException("MrX is Null");
		}

		if(detectives.contains(null)){
			throw new NullPointerException("A detective is Null");
		}

		if(!mrX.isMrX()){
			throw new IllegalArgumentException("No mrX");
		}

		if(detectives.stream().filter(p -> !p.isDetective()).collect(Collectors.toList()).size() > 0){
			throw new IllegalArgumentException("Multiple MrXs");
		}

		ArrayList<Piece> usedPieces = new ArrayList();
		ArrayList<Integer> usedLocations = new ArrayList();
		for (Player p : detectives) {
			if (usedPieces.contains(p.piece())) {
				throw new IllegalArgumentException("Duplicate detectives");
			} else {
				usedPieces.add(p.piece());
			}

			if(usedLocations.contains(p.location())){
				throw new IllegalArgumentException("Duplicate locations");
			}else{
				usedLocations.add(p.location());
			}

			if(p.tickets().get(ScotlandYard.Ticket.SECRET) > 0){
				throw new IllegalArgumentException("detectives should not have secret tickets");
			}
			if(p.tickets().get(ScotlandYard.Ticket.DOUBLE) > 0){
				throw new IllegalArgumentException("detectives should not have double tickets");
			}

			if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty");

		}

		return new MyGameState(setup, ImmutableSet.of(Piece.MrX.MRX), ImmutableList.of(), mrX, detectives);
	}

	private final class MyGameState implements GameState {

		private GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private List<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;


		private MyGameState(
				@Nonnull final GameSetup setup,
				@Nonnull final ImmutableSet<Piece> remaining,
				@Nonnull final ImmutableList<LogEntry> log,
				@Nonnull final Player mrX,
				@Nonnull final List<Player> detectives){
			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;
		}


		@Override public GameSetup getSetup() {  return null; }
		@Override  public ImmutableSet<Piece> getPlayers() { return null; }

		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			return Optional.empty();
		}

		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			return Optional.empty();
		}

		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {
			return null;
		}

		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			return null;
		}

		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			return null;
		}

		@Override public GameState advance(Move move) {  return null;  }
	}
}
