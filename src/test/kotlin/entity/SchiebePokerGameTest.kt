package entity

import kotlin.test.*

/**
 * Unit tests for the [SchiebePokerGame] class
 *
 *
 * */

class SchiebePokerGameTest {

    private lateinit var game: SchiebePokerGame

    @BeforeTest
    fun setup() {
        game = SchiebePokerGame()
    }


    /**Verifies that when game start have list of players empty, moveLog empty, centerCards empty , drawPile empty
     *  , discradPile empty*/
    @Test
    fun `when game start, should start with empty (list of players, moveLog, centerCards , drawPile , discradPile)`() {
        assertTrue(game.players.isEmpty())
        assertTrue(game.moveLog.isEmpty())
        assertTrue(game.centerCards.isEmpty())
        assertTrue(game.drawPile.isEmpty())
        assertTrue(game.discardPile.isEmpty())
    }

    /**Verifies that when game start is currentRound and gameRound and countAction and CurrentPlayer are 0 */
    @Test
    fun `when game start, should start with 0 (currentRound, countAction, gameRound,currentPlayer)` () {
        assertEquals(0, game.currentRound)
        assertEquals(0, game.countAction)
        assertEquals(0, game.gameRound)
        assertEquals(0, game.currentPlayer)
    }

    /**Verifies that the one game can have minimum 2 players*/
    @Test
    fun `game can have 2 players` () {
            game.players.add(Player("Mohammad"))
            game.players.add(Player("Zein"))
            assertEquals(2, game.players.size)
    }

    /**Verifies that the one game can have  3 players*/
    @Test
    fun `game can have 3 players` () {
        game.players.add(Player("Mohammad"))
        game.players.add(Player("Zein"))
        game.players.add(Player("abc"))

        assertEquals(3, game.players.size)
    }

    /**Verifies that the one game can have max 4 players*/
    @Test
    fun `game can have 4 players` () {
        game.players.add(Player("Mohammad"))
        game.players.add(Player("Zein"))
        game.players.add(Player("abc"))
        game.players.add(Player("abcd"))

        assertEquals(4, game.players.size)
    }

    /**Verifies that initial currentPlayer is 0 but should change during game  (Turn) */
    @Test
    fun `currentPlayer is not fix and changed`() {
        game.currentPlayer = 3
        assertEquals(3, game.currentPlayer)
    }

    /**Verifies that initial gameRound is 0 but should increase by 1 after each round (2-7 Rounds) */
    @Test
    fun `gameRound can go up, so next round is available`() {
        game.gameRound++
        assertEquals(1, game.gameRound)
    }

    /**Verifies that initial countAction is 0 but should increase by 1 after each player action (2 Actions max for 1 player) */
    @Test
    fun `countAction can go up and ist max 2 `() {
        game.countAction++
        assertEquals(1, game.countAction)
    }


}