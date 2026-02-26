package entity

/**
 *
 Stores the current state of a Schiebe-Poker game. *
 * This class contains all relevant data to run the game like players, card piles, the current round
 * and the action counter, and it does not contain game logic, logic is modified by the services.
 * @property players  list of players there participating in the game (2–4 players).
 * @property currentPlayer  index of the player whose turn is.
 * @property gameRound  total number of rounds to be played (2–7).
 * @property currentRound  current round number
 * @property countAction number of actions the current player can still perform in this turn (max. 2).
 * @property moveLog  log of all game actions performed during the game.
 * @property drawPile stack of cards used to draw new cards
 * @property discardPile stack of cards where removed cards are placed
 * @property centerCards the three cards currently placed in the center of the table.
 */


class SchiebePokerGame (
    val players: MutableList<Player> = mutableListOf(),
    var currentPlayer: Int = 0,
    var gameRound: Int = 0,
    var currentRound: Int = 0,
    var countAction: Int = 0,
    val moveLog: MutableList<String> = mutableListOf(),
    var playerScores: MutableList<Triple<Int, Int, String>> = mutableListOf(),

    val drawPile : MutableList<Card> = mutableListOf(),
    val discardPile : MutableList<Card> = mutableListOf(),
    val centerCards : MutableList<Card> = mutableListOf()
)