package entity

import kotlin.test.*

/**
 * Unit tests for the [Player] class
 *
 * These tests ensure that player objects are correctly initialized
 */

class PlayerTest {

/**
 * Verifies that the [Player] constructor correctly initializes
 * the player's name.
 *
 * Expected: should "name" matches the given string.
 * */

    @Test
    fun `constructor should correctly assign name `() {
        val player = Player("Mohammad ElZein")

        assertEquals("Mohammad ElZein", player.name)
    }

    /**
     * Verifies that a player starts with empty openCards and hiddenCards.
     */

    @Test
    fun `player should strat with empty openCards and empty hiddenCards `() {
        val player = Player("Mohammad ElZein")
        assertTrue ( player.hiddenCards.isEmpty())
        assertTrue ( player.openCards.isEmpty())

    }

    /**
     * Verifies that exactly two cards can be added to hiddenCards.
     */
    @Test
    fun `hiddenCards should be exactly 2 cards`() {
        val player = Player("Mohammad ElZein")
        player.hiddenCards.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        player.hiddenCards.add(Card(CardSuit.DIAMONDS, CardValue.FIVE))
        assertEquals(2, player.hiddenCards.size)
    }

    /**
     * Verifies that exactly three cards can be added to openCards.
     */
    @Test
    fun `openCards should be exactly 3 cards`() {
        val player = Player("Mohammad ElZein")
        player.openCards.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        player.openCards.add(Card(CardSuit.CLUBS, CardValue.FIVE))
        player.openCards.add(Card(CardSuit.DIAMONDS, CardValue.FIVE))
        assertEquals(3, player.openCards.size)
    }






}