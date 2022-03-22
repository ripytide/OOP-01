package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(GameSetup setup,
											  Player mrX,
											  ImmutableList<Player> detectives){
		//checking validity of parameters given
		if(mrX == null ) throw new NullPointerException("MrX is Null");
		if(!mrX.isMrX()) throw new IllegalArgumentException("No mrX");
		if(detectives.stream().filter(p -> p.isMrX()).toList().size() > 0){
			throw new IllegalArgumentException("Multiple MrXs");
		}
		checkDetectivesValidity(detectives);

		if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty");
		if(setup.graph.nodes().size() == 0) throw new IllegalArgumentException("Graph is empty");

		return new MyGameState(setup, ImmutableSet.of(Piece.MrX.MRX), ImmutableList.of(), mrX, detectives);
	}

	private void checkDetectivesValidity(ImmutableList<Player> detectives){
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
			if(p == null) throw new NullPointerException("A detective is Null");
		}
	}

	private final class MyGameState implements GameState {

		private GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private ImmutableList<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;


		private MyGameState(
				@Nonnull final GameSetup setup,
				@Nonnull final ImmutableSet<Piece> remaining,
				@Nonnull final ImmutableList<LogEntry> log,
				@Nonnull final Player mrX,
				@Nonnull final ImmutableList<Player> detectives){
			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;
			this.winner = calculateWinner();
			this.moves = getAvailableMoves();
		}


		@Override public GameSetup getSetup() { return setup; }
		@Override  public ImmutableSet<Piece> getPlayers() {
			Set<Piece> allPlayers = new HashSet<>(getDetectivePieces(detectives));
			allPlayers.add(mrX.piece());
			return ImmutableSet.copyOf(allPlayers);
		}

		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			return getDetective(detective).map(d -> d.location());
		}

		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			if(piece.isMrX()){
				return Optional.of(t -> mrX.tickets().get(t));
			}
			return getDetective(piece).map(d -> (t -> d.tickets().get(t)));
		}

		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() { return log; }

		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() { return winner; }

		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			if (!winner.isEmpty()) return ImmutableSet.of();
			return getMoves();
		}

		@Override public MyGameState advance(Move move) {
			if (!moves.contains(move)) throw new IllegalArgumentException("Illegal move: " + move);
			return advanceNoCheck(move);
		}

		private boolean isDetectivesTurn(){
			return remaining.stream().noneMatch(Piece::isMrX);
		}

		private boolean isMrXTurn() { return !isDetectivesTurn(); }

		private static void removeUsedTickets(HashMap<ScotlandYard.Ticket, Integer> tickets, Iterable<ScotlandYard.Ticket> usedTickets) {
			usedTickets.forEach(t -> tickets.compute(t, (key, oldTicketCount) -> oldTicketCount - 1));
		}

		private Optional<Player> getDetective(Piece piece){
			return detectives.stream().filter(d -> d.piece() == piece).findFirst();
		}


		private boolean isDetectiveOccupied(int location){
			return detectives.stream().map(Player::location).toList().contains(location);
		}

		private static void giveUsedTicket(HashMap<ScotlandYard.Ticket, Integer> tickets, Iterable<ScotlandYard.Ticket> usedTickets) {
			usedTickets.forEach(t -> tickets.compute(t, (key, oldTicketCount) -> oldTicketCount + 1));
		}

		@Nonnull
		public ImmutableSet<Piece> calculateWinner() {
			ImmutableSet<Move> possibleMoves = getMoves();
			boolean logIsFull = log.size() >= setup.moves.size();
			boolean noAvailableMoves = possibleMoves.isEmpty();
			boolean mrXCaught = isDetectiveOccupied(mrX.location());

			boolean mrXWins = (isMrXTurn() && logIsFull)
						   || (isDetectivesTurn() && (noAvailableMoves));

			boolean detectivesWin = (isMrXTurn() && noAvailableMoves && !logIsFull)
								 || mrXCaught
								 || isDetectiveOccupied(mrX.location());

			if(mrXWins){
				return ImmutableSet.of(mrX.piece());
			}else if(detectivesWin){
				return ImmutableSet.copyOf(detectives.stream().map(d -> d.piece()).collect(Collectors.toSet()));
			}else if(isMrXTurn()){
				MyGameState nextTurn = advanceNoCheck(possibleMoves.stream().collect(Collectors.toList()).get(0));
				return nextTurn.winner;
			}else{
				return ImmutableSet.of();
			}
		}

		@Nonnull
		public ImmutableSet<Move> getMoves() {
			HashSet<Move> availableMoves = new HashSet<>();
			if (isDetectivesTurn()){
				for(Piece p : remaining) {
					Player detective = getDetective(p).get();
					availableMoves.addAll(getSingleMoves(detective, detective.location(), detective.tickets()));
				}
			} else {
				HashSet<Move.SingleMove> availableSingleMoves = getSingleMoves(mrX, mrX.location(), mrX.tickets());
				availableMoves.addAll(availableSingleMoves);

				boolean twoSpacesInLog = log.size() + 1 < setup.moves.size();
				if (mrX.has(ScotlandYard.Ticket.DOUBLE) && twoSpacesInLog){
					availableMoves.addAll(getDoubleMoves(availableSingleMoves, mrX));
				}
			}

			return ImmutableSet.copyOf(availableMoves);
		}

		private HashSet<Move.SingleMove> getSingleMoves(Player player, int source, Map<ScotlandYard.Ticket, Integer> availableTickets){
			HashSet<Move.SingleMove> availableMoves = new HashSet<>();

			for(int destination : setup.graph.adjacentNodes(source)) {
				if (!isDetectiveOccupied(destination)) {
					Set<ScotlandYard.Transport> availableTransport = setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of());

					//adds all available transport moves with correct tickets
					for (ScotlandYard.Transport t : availableTransport) {
						boolean hasCorrectTicket = availableTickets.get(t.requiredTicket()) >= 1;
						if (hasCorrectTicket) {
							availableMoves.add(new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination));
						}
					}

					//adds secret ticket moves without counting Ferries twice
					boolean hasSecretTicket = availableTickets.get(ScotlandYard.Ticket.SECRET) >= 1;
					boolean hasNonFerryTransport = !availableTransport.contains(ScotlandYard.Transport.FERRY) && !availableTransport.isEmpty();
					if (hasSecretTicket && hasNonFerryTransport){
						availableMoves.add(new Move.SingleMove(player.piece(), source, ScotlandYard.Ticket.SECRET, destination));
					}
				}
			}
			return availableMoves;
		}

		public HashSet<Move> getDoubleMoves(HashSet<Move.SingleMove> availableSingleMoves, Player player) {
			HashSet<Move> doubleMoves = new HashSet<>();

			for(Move.SingleMove move1 : availableSingleMoves){
				HashMap<ScotlandYard.Ticket, Integer> availableTickets = new HashMap<>();
				availableTickets.putAll(player.tickets());

				int destination1 = move1.destination;
				int oldTickets = availableTickets.get(move1.ticket);
				availableTickets.put(move1.ticket, oldTickets - 1);
				Set<Move.SingleMove> availableSecondMoves = getSingleMoves(player, destination1, availableTickets);
				for(Move.SingleMove move2 : availableSecondMoves){
					doubleMoves.add(new Move.DoubleMove(mrX.piece(), move1.source(), move1.ticket, destination1, move2.ticket, move2.destination));
				}
			}

			return doubleMoves;
		}

		public MyGameState advanceNoCheck(Move move) {
			// mutable copy of things
			List<Piece> newRemaining = new ArrayList<>(remaining);
			List<LogEntry> newLog = new ArrayList<>(log);
			List<Player> newDetectives = new ArrayList<>(detectives);
			Player newMrX;
			HashMap<ScotlandYard.Ticket, Integer> newMrXTickets = new HashMap<>(mrX.tickets());

			Piece currentPiece = move.commencedBy();
			newRemaining.remove(currentPiece);

			Move.Visitor<Integer> getEndLocationVisitor = new GetEndLocationVisitor();
			if (currentPiece.isDetective()) {
				if(newRemaining.isEmpty()){
					newRemaining.add(mrX.piece());
				}

				Player detective = getDetective(currentPiece).get();
				newDetectives.remove(detective);

				HashMap<ScotlandYard.Ticket, Integer> newDetectiveTickets = new HashMap<>(detective.tickets());
				removeUsedTickets(newDetectiveTickets, move.tickets());
				giveUsedTicket(newMrXTickets, move.tickets());

				newDetectives.add(new Player(currentPiece, ImmutableMap.copyOf(newDetectiveTickets), move.accept(getEndLocationVisitor)));
				newMrX = new Player(mrX.piece(), ImmutableMap.copyOf(newMrXTickets), mrX.location());

				MyGameState partiallyAdvancedState = new MyGameState(setup, ImmutableSet.copyOf(newRemaining), ImmutableList.copyOf(newLog), newMrX, ImmutableList.copyOf(newDetectives));
				if (partiallyAdvancedState.getMoves().isEmpty() && !newRemaining.isEmpty() && !newRemaining.stream().filter(p -> p.isDetective()).collect(Collectors.toList()).isEmpty()) {
					newRemaining = new ArrayList<>(Arrays.asList(mrX.piece()));
				}

			} else {
				newRemaining = getDetectivePieces(detectives);

				int moveNumber = log.size();

				//used in appending log entries
				Move.Visitor<List<LogEntry>> getAdditionalLogEntriesVisitor = new Move.Visitor<List<LogEntry>>() {
					@Override
					public List<LogEntry> visit(Move.SingleMove move) {
						List<LogEntry> newAdditionalLogEntries = new ArrayList<>();
						if(setup.moves.get(moveNumber) == false){
							newAdditionalLogEntries.add(LogEntry.hidden(move.ticket));
						}else{
							newAdditionalLogEntries.add(LogEntry.reveal(move.ticket, move.destination));
						}
						return newAdditionalLogEntries;
					}

					@Override
					public List<LogEntry> visit(Move.DoubleMove move) {
						List<LogEntry> newAdditionalLogEntries = new ArrayList<>();
						if(setup.moves.get(moveNumber) == false){
							newAdditionalLogEntries.add(LogEntry.hidden(move.ticket1));
						}else{
							newAdditionalLogEntries.add(LogEntry.reveal(move.ticket1, move.destination1));
						}

						if(setup.moves.get(moveNumber + 1) == false){
							newAdditionalLogEntries.add(LogEntry.hidden(move.ticket2));
						}else{
							newAdditionalLogEntries.add(LogEntry.reveal(move.ticket2, move.destination2));
						}

						return newAdditionalLogEntries;
					}
				};

				newLog.addAll(move.accept(getAdditionalLogEntriesVisitor));

				removeUsedTickets(newMrXTickets, move.tickets());

				newMrX = new Player(mrX.piece(), ImmutableMap.copyOf(newMrXTickets), move.accept(getEndLocationVisitor));
			}

			return new MyGameState(setup, ImmutableSet.copyOf(newRemaining), ImmutableList.copyOf(newLog), newMrX, ImmutableList.copyOf(newDetectives));
		}

		private List<Piece> getDetectivePieces(ImmutableList<Player> detectives){
			return detectives.stream().map(d -> d.piece()).collect(Collectors.toList());
		}
	}
}
