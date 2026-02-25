package service

import entity.*

class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {

    private fun requireGame(): SchiebePokerGame =
        rootService.currentGame ?: throw IllegalStateException("No active game found.")

    /**
     * verschiebt die Karten in der Mitte nach links.
     *  Linke Karte → Ablagestapel
     *  Mittlere und rechte Karte rücken einen Platz nach links schieben
     *  Eine karte vom Nachziehstapel Karten wird am Recht hinzugefügt
     */
    fun pushLeft() {
        val game = requireGame()

        // Linke Karte (Index 0) auf Ablagestapel und wird automatish die 2 karte aufmitte nach links shieben
        val leftdiscardedCard = game.centerCards.removeAt(0)
        game.discardPile.add(leftdiscardedCard)

        // neue Karte von Nachziehstapel rechts einfügen (in Index 2)
        val newCard = rootService.gameService.drawCard()
        game.centerCards.add(newCard) // fügt am Ende Idx 2

        rootService.gameService.createLogEntry(
            "(\${game.players[game.currentPlayer].name})" +
                    "shifted the cards to the left."
                    + "Discarded: $leftdiscardedCard, New card: $newCard"
        )

        rootService.gameService.consumeAction()//countAction++
        onAllRefreshables { refreshAfterAction() }
    }

    /**
     * Verschiebt die Karten in der Mitte nach rechts
     *  Rechte Karte → Ablagestapel
     *  Mittlere und linke Karte rücken einen Platz nach rechts
     *  Eine Karte vom Nachziehstapel wird am Links hinzugefügt
     */
    fun pushRight() {
        val game = requireGame()

        // Rechte Karte (Index 2) auf Ablagestapel und wird automatish die 2 karte auf mitte nach rechts shieben
        val rightdiscardedCard = game.centerCards.removeAt(2)
        game.discardPile.add(rightdiscardedCard)

        // Neue Karte vom Nachziehstapel links einfügen (Index 0)
        val newCard = rootService.gameService.drawCard()
        game.centerCards.add(0, newCard) // fügt am anfang an Idx 0

        rootService.gameService.createLogEntry(
            "(\${game.players[game.currentPlayer].name})" +
                    "shifted the cards to the right."
                    + "Discarded: $rightdiscardedCard, New card: $newCard"
        )


        rootService.gameService.consumeAction()//countAction++
        onAllRefreshables { refreshAfterAction() }
    }

    /**
     * Spieler verzichtet auf  Tausch.
     * wird  countAction um 1 erhöht trotzdem nix geändert
     */
    fun swapNothing() {
        val game = requireGame()

        rootService.gameService.createLogEntry(
            "(${game.players[game.currentPlayer].name}) " +
                    "passed on the swap."
        )

        rootService.gameService.consumeAction() //countAction++
        onAllRefreshables { refreshAfterAction() }
    }

    /**
     * Tauscht eine offene Karte des Spielers mit einer Karte aus der Mitte.
     *
     * @param playerCardIdx Index der offenen Karte des Spielers (0, 1 oder 2)
     * @param sharedCardIdx Index der Karte in der Mitte (0, 1 oder 2)
     */
    fun swapOneCard(playerCardIdx: Int, sharedCardIdx: Int) {
        val game = requireGame()
        val player = game.players[game.currentPlayer]

        require(playerCardIdx in 0..2) { "playerCardIdx must be between 0 and 2." }
        require(sharedCardIdx in 0..2) { "sharedCardIdx must be between 0 und 2 ." }

        val playerCard = player.openCards[playerCardIdx]
        val centerCard = game.centerCards[sharedCardIdx]

        player.openCards[playerCardIdx] = centerCard
        game.centerCards[sharedCardIdx] = playerCard

        rootService.gameService.createLogEntry(
            "(${player.name}) " +
                    "swapped their open card $playerCard"
                    + "with the center card $centerCard."
        )

        rootService.gameService.consumeAction()//countAction++
        onAllRefreshables { refreshAfterAction() }
    }

    /**
     * Tauscht alle drei offenen Karten des Spielers mit den drei Karten in der Mitte.
     * Links mit Links, Mitte mit Mitte, Rechts mit Rechts
     */
    fun swapAllCards() {
        val game = requireGame()
        val player = game.players[game.currentPlayer]

        // Alle drei Karten tauschen (Index 0, 1, 2)
        for (i in 0..2) {
            val playerCard = player.openCards[i]
            val centerCard = game.centerCards[i]
            player.openCards[i] = centerCard
            game.centerCards[i] = playerCard
        }

        rootService.gameService.createLogEntry(
            "(${player.name}) " +
                    "swapped all three open cards with the center cards."
        )

        rootService.gameService.consumeAction()//countAction++
        onAllRefreshables { refreshAfterAction() }
    }





}