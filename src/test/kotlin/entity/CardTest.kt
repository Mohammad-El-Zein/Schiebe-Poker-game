package entity

import kotlin.test.*

/** Unit tests for the [Card] class
 *
 * These tests ensure the equality , not equality and copy cards of the constructur
 * */

class CardTest {
    /**
     * Verifies that the constructor  correctly assign suit and value
     * */
    @Test
    fun `constructor should correctly assign suit and value`() {
        val card = Card(CardSuit.DIAMONDS, CardValue.FIVE)
        assertEquals(CardSuit.DIAMONDS, card.suit)
        assertEquals(CardValue.FIVE, card.value)
    }


    /**Verifies that the constructor understand the equality of 2 cards if the same suit and value have*/
    @Test
    fun `two cards with same suit and value should be 2 equal cards`() {
        val card1 = Card(CardSuit.CLUBS, CardValue.FIVE)
        val card2 = Card(CardSuit.CLUBS, CardValue.FIVE)
        assertEquals(card1, card2)
    }

    /**Verifies that the constructor understand the non equality of 2 cards if different suit or value have*/
    @Test
    fun `two cards with different suit or different value should not be equal`() {
        val card1 = Card(CardSuit.DIAMONDS, CardValue.FIVE)
        val card2 = Card(CardSuit.SPADES, CardValue.FIVE)
        val card3 = Card(CardSuit.DIAMONDS, CardValue.SEVEN)
        assertNotEquals(card1, card2)
        assertNotEquals(card1, card3)

    }

    /**Verifies that the constructor understand that a copy card with different suit or value is a new card*/
    @Test
    fun `when copy a card but with different suit or value, then create a new card`() {
        val card = Card(CardSuit.DIAMONDS, CardValue.ACE)
        val copy1 = card.copy(suit = CardSuit.HEARTS)
        val copy2 = card.copy(value = CardValue.FIVE)

        assertEquals(CardSuit.HEARTS, copy1.suit)
        assertEquals(CardValue.FIVE, copy2.value)
    }
}