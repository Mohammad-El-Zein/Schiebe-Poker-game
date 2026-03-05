package service

import entity.*
import kotlin.test.*

/**
 * Unit Test für [GameService].
 * Testet alle Methoden des GameService mit fehler fälle.
 */

class GameServiceTest {

    private lateinit var rootService: RootService

    /**
     * Wird vor jedem Test aufgerufen.

     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
    }

    /**
     * Erstellt ein gültiges Spiel with 2 Spieler und 2 Runden für überprufen ob alles richtig initiallisiert.
     */
    @Test
    fun testCreateGame() {
        val numberofPlayers = listOf("Mohammad", "Zein")
        rootService.gameService.createGame(numberofPlayers, 2)
        val game = rootService.currentGame

        assertNotNull(game)
        assertEquals(2, game.players.size)
        assertEquals(2, game.gameRound)
        assertEquals(0, game.currentPlayer)
        assertEquals(1, game.currentRound)

        game.players.forEach { player ->
            assertEquals(2, player.hiddenCards.size)
            assertEquals(3, player.openCards.size)
        }

        assertEquals(3, game.centerCards.size)

        val expectedDraw = 52 - (numberofPlayers.size * 5) - 3
        assertEquals(expectedDraw, game.drawPile.size)

        assertTrue(game.discardPile.isEmpty())


    }

    /**
     * Fehlerfall:
     * invalid Spieler nmbr (<2 oder >4) oder invalid Round nmbr (<2 oder >7) wirft IllegalArgumentException.
     */
    @Test
    fun `test createGame rejects with invalid players count or invalid Round number`() {
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.createGame(listOf("mohammad"), 2)
        }
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.createGame(listOf("mohammad","zein","abc","abcd","a"), 2)
        }
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.createGame(listOf("mohammad","zein"), 1)
        }
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.createGame(listOf("mohammad","zein"), 8)
        }
    }

    /**
     * Nach createGame und setUpCards aufrufen muss checken ob alle verteilte karten 52 cards sind
     * wir haben 2 Spieler also  2*5 (spielerKarten) + 3 (offenemitteKArten) + drawPile  mussen = 52 sein
     */
    @Test
    fun `test the sum of cards after setUpCards`() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 7)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        val totalCards = game.players.sumOf { it.hiddenCards.size + it.openCards.size } +
                game.centerCards.size + game.drawPile.size

        assertEquals(52, totalCards)
    }

    /**
     * überprufen ob consumeAction erhöht countAction auf 1.
     */
    @Test
    fun `countAction should increased by 1 after consumeAction call`() {
        rootService.gameService.createGame(listOf("Mo", "Zein"), 2)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        rootService.gameService.consumeAction()
        assertEquals(1, game.countAction)
    }

    /**
     * überprufen ob nach aufrufen endTurn, wechselt zum nächsten Spieler.
     */
    @Test
    fun `should currentPlayer index by 1 increased after endTurn call`() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 5)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        val firstPlayer = game.currentPlayer // hat index 0

        rootService.gameService.consumeAction()
        rootService.gameService.consumeAction()
        rootService.gameService.endTurn()

        assertEquals(firstPlayer + 1, game.currentPlayer) // index 1
    }

    /**
     * muss endTurn beim letzten Spieler , currentPlayer auf 0 setzt und erhöht currentRound.
     */
    @Test
    fun `should endTurn for last player resets cuurrentPlayer to 0 and increments currentRound `() {
        rootService.gameService.createGame(listOf("Mo", "Zein"), 5)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }


        rootService.gameService.startTurn()
        rootService.gameService.consumeAction()
        rootService.gameService.consumeAction()
        rootService.gameService.endTurn() // Spieler mit index 0 hat 2 actions gemacht und seine Zug beendet


        rootService.gameService.startTurn()
        rootService.gameService.consumeAction()
        rootService.gameService.consumeAction()
        rootService.gameService.endTurn()// Spieler mit index 1 hat 2 actions gemacht und seine Zug beendet

        assertEquals(0, game.currentPlayer)
        assertEquals(2, game.currentRound)
    }

    /**
     * wenn endTurn aufruden  ohne 2 Aktionen zu machen,  wirft IllegalArgumentException.
     */
    @Test
    fun `should endTurn with less than 2 Actions  IllegalArgumentException gives`() {
        rootService.gameService.createGame(listOf("M", "z"), 2)

        rootService.gameService.consumeAction() // nur 1 Aktion also kleiner als 2 aktionen

        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.endTurn()
        }
    }

    /**
     * Wenn Nachziehstapel leer ist, wird Ablagestapel micht mit Nachziehstapel .
     */
    @Test
    fun `If the draw pile is empty, the discard pile is reshuffled and used as the new draw pile`() {
        rootService.gameService.createGame(listOf("Mo", "Z"), 7)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        // Nachziehstapel leeren und Karten in Ablagestapel legen
        val cards = game.drawPile.toList()
        game.drawPile.clear()
        game.discardPile.addAll(cards)

        rootService.playerActionService.pushLeft()

        assertTrue(game.discardPile.isEmpty() || game.drawPile.isNotEmpty())
    }

    /**
     * fehlerfall:  wenn beide Stapel leer sind während laufen des spiel, wirft IllegalArgumentException.
     */
    @Test
    fun ` drawPile and discardPile are empty during the game`() {
        rootService.gameService.createGame(listOf("M", "Z"), 2)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        game.drawPile.clear()
        game.discardPile.clear()   // hier clear machen nur für testing

        assertFailsWith<IllegalArgumentException> {
            rootService.playerActionService.pushLeft()
        }
    }

    /**
     *  calculateRanking gibt Liste mit genau gleiche anzahl auf spieler und ranking dann für jede speieler ein ranking
     */
    @Test
    fun `should for each player , a score`() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 2)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        rootService.gameService.endGame()
        assertEquals(game.players.size, game.playerScores.size)
    }

    /**
     * Muss Spieler mit Royal Flush vor Spieler mit Straight Flush stehen.
     */
    @Test
    fun calculateRanking() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 3)
        val game = checkNotNull(rootService.currentGame) { "Game should not be null after createGame" }

        val mohammad = game.players.first { it.name == "Mohammad" }
        val zein = game.players.first { it.name == "Zein" }

        mohammad.hiddenCards.clear()
        mohammad.openCards.clear()
        mohammad.hiddenCards.addAll(listOf(
            Card(CardSuit.DIAMONDS, CardValue.QUEEN),
            Card(CardSuit.DIAMONDS, CardValue.KING)
        ))
        mohammad.openCards.addAll(listOf(
            Card(CardSuit.DIAMONDS, CardValue.TEN),
            Card(CardSuit.DIAMONDS, CardValue.ACE),
            Card(CardSuit.DIAMONDS, CardValue.JACK)
        ))

        zein.hiddenCards.clear()
        zein.openCards.clear()
        zein.hiddenCards.addAll(listOf(
            Card(CardSuit.SPADES, CardValue.FIVE),
            Card(CardSuit.SPADES, CardValue.FOUR)
        ))
        zein.openCards.addAll(listOf(
            Card(CardSuit.SPADES, CardValue.SIX),
            Card(CardSuit.SPADES, CardValue.EIGHT),
            Card(CardSuit.SPADES, CardValue.SEVEN)
        ))

        rootService.gameService.endGame()

        val firstPlace = game.playerScores.first { it.second == 1 }
        val secondPlace = game.playerScores.first { it.second == 2 }

        assertEquals("Mohammad", game.players[firstPlace.first].name)
        assertEquals("Zein", game.players[secondPlace.first].name)
    }

}