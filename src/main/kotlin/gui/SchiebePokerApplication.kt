package gui

import service.Refreshable
import tools.aqua.bgw.core.BoardGameApplication
import service.RootService

import java.util.Timer
import java.util.TimerTask

/**
 * Represents the main application for the SoPra board game.
 * The application initializes the [RootService] and displays the scenes.
 */

class SchiebePokerApplication : BoardGameApplication("SoPra Game"), Refreshable {

    /**
     * The root service instance. This is used to call service methods and access the entity layer.
     */
    val rootService: RootService = RootService()


    /**
     * The main game scene displayed in the application.
     */

    private val newGameMenuScene    = NewGameMenuScene(rootService)
    private val intermissionScene     = IntermissionScene(rootService)
    private val gameScene       = SchiebePokerGameScene(rootService)
    private val scoreBoardScene  = ScoreboardScene(rootService)

    // Index Spieler der gerade muss seine Karten vorshau sehen
    private var previewSpielerIdx = 0

    private var previewIsDone = false


    /**
     * Initializes the application by displaying the Scenes.
     */


    init {
        // beim Service registrieren ,da können Refreshes ankommen
        rootService.addRefreshable(gameScene)
        rootService.addRefreshable(scoreBoardScene)

        // Wenn Spiel fertig ist dann score anzeigen
        rootService.addRefreshable(object : service.Refreshable {
            override fun refreshAfterTurnEnd() {
                val game = rootService.currentGame ?: return
                showNextPlayerHandoff(game.currentPlayer)  //
            }

                override fun refreshAfterGameEnd() {
                    scoreBoardScene.updateResults()
                    showMenuScene(scoreBoardScene)
                }
        })

        // Hauptmenü: Spiel starten
        newGameMenuScene.startButton.onMouseClicked = {
            newGameMenuScene.startGame()
            previewSpielerIdx = 0
            previewIsDone = false
            gameScene.updateView()
            hideMenuScene()
            showGameScene(gameScene)
            intermissionScene.updatePlayerName(
                rootService.currentGame?.players?.getOrNull(0)?.name ?: "Spieler 1")
            showMenuScene(intermissionScene)
        }

        // Übergabe-Bildschirm: wenn Spieler bereit dann wird verdeckte Karten kurz zeigen
        intermissionScene.readyButton.onMouseClicked = onMouseClicked@{
            val game = rootService.currentGame ?: return@onMouseClicked

            if (!previewIsDone) {
                // Vorschau-Phase: verdeckte Karten 5 Sek zeigen
                gameScene.showHiddenCardsFor(previewSpielerIdx)
                hideMenuScene()
                showGameScene(gameScene)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        previewSpielerIdx++
                        if (previewSpielerIdx < game.players.size) {
                            gameScene.updateView()
                            showNextPlayerHandoff(previewSpielerIdx)
                        } else {
                            previewSpielerIdx = 0
                            previewIsDone = true
                            gameScene.updateView()
                            hideMenuScene()
                            showGameScene(gameScene)
                        }
                    }
                }, 5000)

            } else {
                // wird Normaler Zugwechsel, direkt weiterspielen und kein Timer
                hideMenuScene()
            }
        }

        // Einzelne Karte tauschen wenn speielr gewählt ein hand karte und mitte karte
        gameScene.btnSwapOne.onMouseClicked = {
            gameScene.startSwapOneMode()
            gameScene.onSwapOneReady = { myCard, tableCard ->
                rootService.playerActionService.swapOneCard(myCard, tableCard)
            }
        }

        // Alle drei Karten tauschen
        gameScene.btnSwapAll.onMouseClicked = {
            rootService.playerActionService.swapAllCards()
        }

        // nix  tauschen
        gameScene.btnSwapNone.onMouseClicked = {
            rootService.playerActionService.swapNothing()
        }

        // Mittelkarten nach links schieben
        gameScene.btnShiftLeft.onMouseClicked = {
            rootService.playerActionService.pushLeft()
        }

        // Mittelkarten nach rechts schieben
        gameScene.btnShiftRight.onMouseClicked = {
            rootService.playerActionService.pushRight()
        }


        //  neues Spiel starten
        scoreBoardScene.newGameButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(newGameMenuScene)
        }

        // spiel schliessen
        scoreBoardScene.exitButton.onMouseClicked = {
            System.exit(0)
        }

        showMenuScene(newGameMenuScene)
        show()
    }

    /**
     * Setzt `previewIdx` auf nächste Spieler,
     * also aktualisiert Name im Übergabe Bildschirm und zeigt es an
     *
     * @param idx Index des Spielers der als nächstes seine Karten sehen soll
     */
    private fun showNextPlayerHandoff(idx: Int) {
        val game = rootService.currentGame ?: return
        previewSpielerIdx = idx
        intermissionScene.updatePlayerName(game.players[idx].name)
        showMenuScene(intermissionScene)
    }
}