package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;

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

		if(detectives.stream().filter(p -> !p.isDetective()).toList().size() > 0){
			throw new IllegalArgumentException("Multiple MrXs");
		}

		ArrayList<Piece> usedPieces = new ArrayList<>();
		ArrayList<Integer> usedLocations = new ArrayList<>();
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
		}

		if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty");

		if(setup.graph.nodes().size() == 0) throw new IllegalArgumentException("Graph is empty");

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


		@Override public GameSetup getSetup() { return setup; }
		@Override  public ImmutableSet<Piece> getPlayers() {
			Set<Piece> allPlayers = new HashSet<>();
			for(Player d : detectives){
				allPlayers.add(d.piece());
			}
			allPlayers.add(mrX.piece());

			ImmutableSet<Piece> players = ImmutableSet.copyOf(allPlayers);
			return players;
		}

		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			Optional<Player> actualDetective = getDetective(detective);
			if (actualDetective.isEmpty()) return Optional.empty();
			return Optional.of(actualDetective.get().location());
		}

		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			Optional<Player> actualDetective = getDetective(piece);
			if(actualDetective.isEmpty()) {
				if(piece.isMrX()){
					return Optional.of(t -> mrX.tickets().get(t));
				}
				return Optional.empty();
			}
			return Optional.of(t -> actualDetective.get().tickets().get(t));
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

		private Optional<Player> getDetective(Piece piece){
			return detectives.stream().filter(d -> d.piece() == piece).findFirst();
		}
	}
}
