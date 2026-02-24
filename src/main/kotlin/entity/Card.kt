package entity
/**
 * Stores a single card
 * Each card has a fixed suit and value and cards used in  draw pile,
 *  * discard pile, center, and player hands
 *
 *  @property suit card's suit (hearts, clubs, spades, diamonds)
 *  @property value card's face value ( exmpl: seven, king, five, ace)*/
data class Card  (
    val suit: CardSuit,
    val value: CardValue
)