package entity


/** Stores a player in the ScheibePoker game
 *
 * Each player has a name, two hidden cards and three open cards.
 *
 * @property name  player's name
 * @property hiddenCards  two hidden cards of the player
 * @property openCards the three open cards of the player
 *

 * */


class Player (
    val name : String,
    val hiddenCards : MutableList<Card> = mutableListOf(),   /** die spieler 2 verdeckte karten*/
    val openCards : MutableList<Card> = mutableListOf()      /** spieler 3   offenes karten*/
)