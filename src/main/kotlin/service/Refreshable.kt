package service

import entity.Player

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * GUI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable {

    /**aufrufen nachdem ein Spiel start*/
    fun refreshAfterGameStart() {

    }
    /**aufrufen nachdem ein Turn beginnt*/
    fun refreshAfterTurnStart() {

    }
    /**aufrufen nachdem ein Turn endet*/
    fun refreshAfterTurnEnd() {

    }
    /**aufrufen nachdem ein Spiel endet*/
    fun refreshAfterGameEnd() {

    }
    /**aufrufen nachdem Nachziehstapel ist wieder voll*/
    fun refreshAfterRefillStack() {

    }
    /**aufrufen nachdem Action ausführen von ein Spieler */
    fun refreshAfterAction() {

    }


}