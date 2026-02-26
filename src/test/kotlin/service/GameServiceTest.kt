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
        val game = rootService.currentGame!!

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
        val game = rootService.currentGame!!

        rootService.gameService.consumeAction()
        assertEquals(1, game.countAction)
    }

    /**
     * überprufen ob nach aufrufen endTurn, wechselt zum nächsten Spieler.
     */
    @Test
    fun `should currentPlayer index by 1 increased after endTurn call`() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 5)
        val game = rootService.currentGame!!

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
        val game = rootService.currentGame!!


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
        val game = rootService.currentGame!!

        // Nachziehstapel leeren und Karten in Ablagestapel legen
        val cards = game.drawPile.toList()
        game.drawPile.clear()
        game.discardPile.addAll(cards)

        val card = rootService.gameService.drawCard()

        assertNotNull(card)
        assertTrue(game.drawPile.isNotEmpty())
    }

    /**
     * fehlerfall:  wenn beide Stapel leer sind während laufen des spiel, wirft IllegalArgumentException.
     */
    @Test
    fun ` drawPile and discardPile are empty during the game`() {
        rootService.gameService.createGame(listOf("M", "Z"), 2)
        val game = rootService.currentGame!!

        game.drawPile.clear()
        game.discardPile.clear()   // hier clear machen nur für testing

        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.drawCard()
        }
    }

    /**
     *  calculateRanking gibt Liste mit genau gleiche anzahl auf spieler und ranking dann für jede speieler ein ranking
     */
    @Test
    fun `should for each player , a score`() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 2)
        val game = rootService.currentGame!!

        val ranking = rootService.gameService.calculateRanking()

        assertEquals(game.players.size, ranking.size)
    }

    /**
     * muss Spieler mit zb Royal Flush  steht vor Spieler mit straight Flush .
     */
    @Test
    fun claculateRAnking() {
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 3)
        val game = rootService.currentGame!!

       //sei Mohammad zb hat index 0 , und bekommt Royal Flush
        game.players[0].hiddenCards.addAll(listOf(
            Card(CardSuit.DIAMONDS, CardValue.QUEEN),
            Card(CardSuit.DIAMONDS, CardValue.KING)
        ))
        game.players[0].openCards.addAll(listOf(
            Card(CardSuit.DIAMONDS, CardValue.TEN),
            Card(CardSuit.DIAMONDS, CardValue.ACE),
            Card(CardSuit.DIAMONDS, CardValue.JACK)
        ))

        // Sei Zein hat index 1 und bekommt Straight Flush
        game.players[1].hiddenCards.addAll(listOf(
            Card(CardSuit.SPADES, CardValue.FIVE),
            Card(CardSuit.SPADES, CardValue.FOUR)
        ))
        game.players[1].openCards.addAll(listOf(
            Card(CardSuit.SPADES, CardValue.SIX),
            Card(CardSuit.SPADES, CardValue.EIGHT),
            Card(CardSuit.SPADES, CardValue.SEVEN)
        ))

        val ranking = rootService.gameService.calculateRanking()
        assertEquals("Mohammad", ranking[0].name)
        assertEquals("Zein", ranking[1].name)
    }










}