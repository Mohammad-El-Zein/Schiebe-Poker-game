package service

import  entity.SchiebePokerGame
/**
 * The root service class is responsible for managing services and the entity layer reference.
 * This class acts as a central hub for every other service within the application.
 *
 */
class RootService {
    /** aktuelle Spielzustand ist immer null wenn die Spiel laüft noch nicht. */
    var currentGame: SchiebePokerGame? = null


        /** enthält alle Spiel logik wie Karten,Turn usw... */
        val gameService = GameService(this)

        /** enthällt alle Spiel actions wie nach links Schieben usw... */
        val playerActionService = PlayerActionService(this)

        /**
         * Registriert einen Refreshable-UI-Listener bei allen Services, die Aktualisierungsereignisse auslösen.
         * Diese Methode sollte einmal pro Bildschirm aufgerufen werden, das auf Zustandsänderungen reagieren soll.
         */
        fun addRefreshable(newRefreshable: Refreshable) {
            gameService.addRefreshable(newRefreshable)
            playerActionService.addRefreshable(newRefreshable)
        }
    }

