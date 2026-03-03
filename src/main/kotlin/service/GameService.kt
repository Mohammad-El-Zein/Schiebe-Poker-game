package service

import entity.*

/**
 * GameService steuert der Ablauf des SchiebePokerGame.
 * also dieses klasse kümmert um das Erstellen, Spielzüge, aktionen, Scores und die Regeln.
 *
 * @property rootService ist die referenz auf den RootService, aktuelle Game wird mit dieses rootService zugeriffen
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Startet ein neues Spiel.
     * @param playerNames eine Liste mit 2 bis 4 Namen.
     * @param gameRound  zahl der  spielenden Runden 2 bis 7.
     * @throws IllegalArgumentException ob Anzahl der Spieler oder Runden  nicht in der gültige Liste ist.
     */

    fun createGame(playerNames : List<String>, gameRound : Int) {

        require(playerNames.size in 2..4) { "Players count must be  'minimum 2'..'4 Maximum' ." }
        require(gameRound in 2..7) { "gameRound must be 'minimum 2'..'7 Maximum'" }

        val game = SchiebePokerGame()

        game.gameRound = gameRound

        game.currentRound = 1

        val startIndex = playerNames.indices.random()

        val reorderedNames = playerNames.subList(startIndex, playerNames.size) + playerNames.subList(0, startIndex)
        // Liste neu anordnen: Startspieler=Index 0,rechts=1, gegenüber=2, links=3

        game.players.addAll(reorderedNames.map { Player(it) })


        game.currentPlayer = 0 // startspieler ist immer Index 0

        rootService.currentGame = game // unsere spiel aktiv

        setUpCards() // in dieses aufrufung wird auch createDrawStack intren aufgerufen
        onAllRefreshables { refreshAfterGameStart() }
        startTurn()

    }

    /**
     * Hilfsfunktion stellt sicher dass ein Spiel aktiv ist, bevor wir eine Aktion ausführen.
     * meldet fehler wenn kein spiel gestartet

     */
    private fun requireGame(): SchiebePokerGame =
        rootService.currentGame ?: throw IllegalStateException("No active game found so should call createGame first.")


    /**
     * bereitet  Züge des aktuellen Spieler vor, mit genau 2 aktionen für jede spieler Turn.
     *
     */
    fun startTurn() {
        val game = requireGame()
        game.countAction = 0
        onAllRefreshables { refreshAfterTurnStart() }
    }

    /**
     * hilfsfunktion wird nach ejder action aufrufen
     * Registrieren die ausgeführte Aktion des aktuellen Spieler mit max 2 Actionen durchführen
     * @throws IllegalArgumentException wenn spieler bereit zwei Aktionen ausgeführt hat.
     */
    fun consumeAction() {
        val game = requireGame()
        require(game.countAction < 2) { "Player has already performed 2 actions." }
        game.countAction++
        onAllRefreshables { refreshAfterAction() }
    }

    /**
    * Beendet Turn des aktuellen Spieler und wechselt zum nächsten.
    * Wenn alle Spieler seinen  2 Actionen in die gleiche Runde fertig machen, beginnt eine neue Runde.
    */
    fun endTurn() {
        val game = requireGame()

        require(game.countAction == 2) { "Player must exactly 2 actions before ending his turn."}

        if (game.currentPlayer < game.players.size - 1) { //hier currentPlayer als index also erste player hat index 0
        game.currentPlayer++
        } else {
            game.currentPlayer = 0
            game.currentRound++
        }

        onAllRefreshables { refreshAfterTurnEnd() }
        if (game.currentRound > game.gameRound) endGame()
        else startTurn()
    }


    /**
     * Erstellt alle  52 Spielkarten und mischt ihn.
     */
    private fun createDrawStack() {
        val game = requireGame()

        val deck = mutableListOf<Card>()
        for (suit in CardSuit.values()) {
            for (value in CardValue.values()) {
                deck.add(Card(suit, value))
            }
        }

        deck.shuffle()
        game.drawPile.clear()
        game.drawPile.addAll(deck)
    }

    /**
     * Verteilt karten an alle Spieler und in die Mitte.
     * 5 karte für jeder spieler , 2 verdeckte karten und 3 offene karten
     * 3 offene karten in die Mitte legen
     * dann Nachziehstappel ist die restliche karten
     */
    private fun setUpCards() {
        val game = requireGame()
        createDrawStack()
        val deck = game.drawPile
        // jede player hat 2 hidden + 3 open
        for (player in game.players) {
            repeat(2) { player.hiddenCards.add(deck.removeLast()) }
            repeat(3) { player.openCards.add(deck.removeLast()) }
        }

        repeat(3) { game.centerCards.add(deck.removeLast()) }

    }


    /**
     *wenn Nachziehstapel leer ist, dann mischt den Ablagestapel  in den Nachziehstapel.
     * automatish aurufen wenn Nachziehstapel leer sind
     */
     fun refillDrawStack() {
        val game = requireGame()

        //wenn ablagestpel ist leer dann
        require(game.discardPile.isNotEmpty()) {
            "Cannot refill draw pile because discard pile is also empty."
        }

        game.drawPile.addAll(game.discardPile)
        game.discardPile.clear()
        game.drawPile.shuffle()


        onAllRefreshables { refreshAfterRefillStack() }
    }

    /**hilfsfunktion:aurufen wenn push right oder left mit überprufung von draw pile ob emtpy ist*/
    fun drawCard(): Card {
        val game = requireGame()

        if (game.drawPile.isEmpty()) {
            refillDrawStack()
        }

        return game.drawPile.removeLast() //nimmt letzte karte auf nachziehstapel
    }



    /**
     * trägt den aktuellen Spielzug des aktiven Spielers in das LogEntry ein.
     *
     */
    fun createLogEntry(message: String) {
        val game = requireGame()
        game.moveLog.add(message)
    }

    /**
     * Beendet das Spiel und zeigt das Endergebnis an.
     */
    fun endGame() {
        val game = requireGame()

        calculateScore()

        createLogEntry("Game ended after ${game.gameRound} rounds.")

        onAllRefreshables { refreshAfterGameEnd() }

    }


    /**
     * Berechnet die Rangliste der Spieler basierend auf ihrer hand karten stärke.
     * @return Eine sortierte Liste des Spieler vom Gewinner zum Verlierer.
     */
    private fun calculateScore() {
        val game = requireGame()

        val sortedPlayers =
            game.players.sortedByDescending { calculateHandCards(it) }

        game.playerScores.clear()

        sortedPlayers.forEachIndexed { platzierung, player ->
            val playerIndex = game.players.indexOf(player)
            val handName = handValueToString(calculateHandCards(player))

            game.playerScores.add(
                Triple(playerIndex, platzierung + 1, handName)
            )
        }
    }

    private fun handValueToString(value: Int): String = when (value) {
        9    -> "Royal Flush"
        8    -> "Straight Flush"
        7    -> "Vierling"
        6    -> "Full House"
        5    -> "Flush"
        4    -> "Straße"
        3    -> "Drilling"
        2    -> "Zwei Paare"
        1    -> "Ein Paar"
        else -> "Höchste Karte"
    }

    /**
    * Berechnet den Wert der Kartenkombination eines Spielers (z.B. Paar, Flush, Straße).
     *
     * Rangliste:
     * 9 = Royal Flush
     * 8 = Straight Flush
     * 7 = Vierling
     * 6 = Full House
     * 5 = Flush
     * 4 = Straße
     * 3 = Drilling
     * 2 = Zwei Paare
     * 1 = Ein Paar
     * 0 = Höchste Karte
     *
     * @param player der zu bewertende Spieler.
     * @return numerischer Wert der Handstärke.
     */
    private fun calculateHandCards(player: Player): Int {
        val hand = player.hiddenCards + player.openCards // 5 karten für jede spieler
        val values = hand.map { valuesToInt(it.value) } // 5 hand karten in Int wechseln
        val suits = hand.map { it.suit } // liste der farben

        val valueCounts = values.groupingBy { it }.eachCount() // jede vaalue wie viel mal es gibt
        val counts = valueCounts.values.sortedDescending() // zb (4,1): 4 karten gleiche value, 1karte andere value

        val isSameSuits = suits.distinct().size == 1 //gleiche suit
        val sortedValues = values.sorted()
        val isStraightedValue = sortedValues.last() - sortedValues.first() == 4//sortieres value,zb 2 bis 6,6-2=4,size=5
                && sortedValues.distinct().size == 5


        val aceStraight = sortedValues == listOf(2, 3, 4, 5, 14) //Ass hat wert 14 aber kann reinfolge (ass,2,3,4)

        val isStraight = isStraightedValue || aceStraight

        return when {
            isSameSuits && isStraight && values.contains(14) && values.contains(10) -> 9 // Royal Flush
            isSameSuits && isStraight       -> 8 // Straight Flush
            counts == listOf(4, 1)        -> 7 // Four of a Kind
            counts == listOf(3, 2)       -> 6 // Full House
            isSameSuits                         -> 5 // Flush
            isStraight                    -> 4 // Straight
            counts == listOf(3, 1, 1)        -> 3 // Three of a Kind
            counts == listOf(2, 2, 1)       -> 2 // Two Pair
            counts == listOf(2, 1, 1, 1)       -> 1 // one Pair
            else                             -> 0 //  High Card
        }
    }

    /**
     * Wandelt Kartenwerte von ENUM  in Zahlen um (z.B. ACE -> 14).
     */
    private fun valuesToInt(value: CardValue): Int = when (value) {
        CardValue.TWO   -> 2
        CardValue.THREE -> 3
        CardValue.FOUR  -> 4
        CardValue.FIVE  -> 5
        CardValue.SIX   -> 6
        CardValue.SEVEN -> 7
        CardValue.EIGHT -> 8
        CardValue.NINE  -> 9
        CardValue.TEN   -> 10
        CardValue.JACK  -> 11
        CardValue.QUEEN -> 12
        CardValue.KING  -> 13
        CardValue.ACE   -> 14
    }




}