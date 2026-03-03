package service

import kotlin.test.*

/**
 * Unit Test für [PlayerActionService].
 * Testet alle Methoden des PlayerActionService mit fehler fälle.
 */

class PlayerActionServiceTest {

    private lateinit var rootService: RootService

    /**
     * Wird vor jedem Test aufgerufen, dann automatish initialisert neuen Rootservice ,
     * und created ein game mit 2 spieler und 2 runden
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        rootService.gameService.createGame(listOf("Mohammad", "Zein"), 2)
    }


    /**
     * muss  pushLeft  alle Karten nach links verschieben, mit Linke Karte (idx 0)  abgelegt ,
     *  2 andere mitte karte nach links schieben und neue Karte kommt am recht an Index 2.
     * -also muss in  Mitte immer 3 Karten gibt es
     */
    @Test
    fun testPushLeft() {
        val game = rootService.currentGame!!

        val leftCard = game.centerCards[0]  // muss abgeleget
        val middleCard = game.centerCards[1]  // muss schieben nach Index 0
        val rightCard = game.centerCards[2]  // muss schieben nach Index 1
        val newCard = game.drawPile.last()  // muss am recht hinzu

        rootService.playerActionService.pushLeft()


        assertEquals(leftCard, game.discardPile.last())


        assertEquals(middleCard, game.centerCards[0]) // war idx 1 , jetzt idx 0
        assertEquals(rightCard, game.centerCards[1]) // war idx 2 jetzt idx 1
        assertEquals(newCard, game.centerCards[2])  // neue karte wird am recht hinzu idx 2

        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht


        assertEquals(1, game.countAction)  // countAction muss erhöht
    }

    /**
    * muss  pushRight  alle Karten nach rechts verschieben, mit Rechte Karte (idx 2)  abgelegt ,
    *  2 andere mitte karte nach rechts schieben und neue Karte kommt am links  an Index 0.
    * -also muss in  Mitte immer 3 Karten gibt es
    */
    @Test
    fun testPushRight() {
        val game = rootService.currentGame!!

        val leftCard = game.centerCards[0]  // muss schieben nach Index 1
        val middleCard = game.centerCards[1]  // muss schieben nach Index 2
        val rightCard = game.centerCards[2]  // muss abgelegt wird
        val newCard = game.drawPile.last()  // muss am links hinzu

        rootService.playerActionService.pushRight()

        assertEquals(rightCard, game.discardPile.last())


        assertEquals(middleCard, game.centerCards[2]) // war idx 1 , jetzt idx 2
        assertEquals(leftCard, game.centerCards[1]) // war idx 0 jetzt idx 1
        assertEquals(newCard, game.centerCards[0])  // neue karte wird am links hinzu idx 0

        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht


        assertEquals(1, game.countAction)  // countAction muss erhöht
    }

    /**
     * wenn pushLeft  ist aufgerufen und Nachziehstapel leer ist ,
     * muss Ablagestapel mit Nachziehstapel mischen.
     */
    @Test
    fun ` should add discardPile with drawPile if drawPile empty by pushLeft call `() {
        val game = rootService.currentGame!!

        // Nachziehstapel leeren, Karten in Ablagestapel
        val cards = game.drawPile.toList()
        game.drawPile.clear()
        game.discardPile.addAll(cards)

        rootService.playerActionService.pushLeft()


        assertEquals(3, game.centerCards.size)
        assertEquals( 1, game.discardPile.size )
        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht

    }

    /**
     * wenn pushRight  ist aufgerufen und Nachziehstapel leer ist ,
     * muss Ablagestapel mit Nachziehstapel mischen.
     */
    @Test
    fun ` should add discardPile with drawPile if drawPile empty by pushRight call `() {
        val game = rootService.currentGame!!

        val cards = game.drawPile.toList()
        game.drawPile.clear()
        game.discardPile.addAll(cards)

        rootService.playerActionService.pushRight()


        assertEquals(3, game.centerCards.size)
        assertEquals( 1, game.discardPile.size )
        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht

    }

    /**
     * muss IllegalArgumentException melden wenn egal welche action aufrufen bei countAction == 2
     */
    @Test
    fun ` if no Actions left should IllegalArgumentException after any action call `() {
        rootService.gameService.consumeAction()
        rootService.gameService.consumeAction()

        assertFailsWith<IllegalArgumentException> {
            rootService.playerActionService.pushRight()
        }
    }

    /**
     * wenn swapNothing aufgerufen , muss  keine Karten veränderung gibt und muss countAction um 1 erhöhen.
     */
    @Test
    fun testSwapNothing() {
        val game = rootService.currentGame!!
        val player = game.players[game.currentPlayer]

        val playerCard = player.openCards.toList()
        val centerCard = game.centerCards.toList()

        rootService.playerActionService.swapNothing()


        assertEquals(playerCard, player.openCards)
        assertEquals(centerCard, game.centerCards)
        assertEquals(1, game.countAction)
        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht

    }

    /**
     * mit swapOneCard tauscht spieler ein hand Karte  mit Mittel karte
     * hier zb hand card id 2 mit mittel akrte idx 0
     */
    @Test
    fun testSwapOneCard() {
        val game = rootService.currentGame!!
        val player = game.players[game.currentPlayer]

        val playerCard = player.openCards[2]
        val centerCard = game.centerCards[0]

        rootService.playerActionService.swapOneCard(2, 0)

        assertEquals(centerCard, player.openCards[2])
        assertEquals(playerCard, game.centerCards[0])

        assertEquals(1, game.countAction) // count action erhöht wie immer
        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht


    }


    /**
     * mit swapAllCards wird alle 3 handKarten mit gleiche idx auf 3 mitteleKarten getauscht
     * zb idx 0 in handCArd mit idx 0 in mitteleCard
     */
    @Test
    fun testSwapAllCards() {
        val game = rootService.currentGame!!
        val player = game.players[game.currentPlayer]

        val playerCard = player.openCards.toList()
        val centerCard = game.centerCards.toList()

        rootService.playerActionService.swapAllCards()

        assertEquals(playerCard[0], game.centerCards[0])
        assertEquals(playerCard[1], game.centerCards[1])
        assertEquals(playerCard[2], game.centerCards[2])
        assertEquals(centerCard[0], player.openCards[0])
        assertEquals(centerCard[1], player.openCards[1])
        assertEquals(centerCard[2], player.openCards[2])

        assertEquals(1, game.countAction)
        assertEquals(3, game.centerCards.size) // muss immer 3 Karten in mitte steht


    }







}