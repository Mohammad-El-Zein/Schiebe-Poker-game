package gui

import tools.aqua.bgw.core.BoardGameApplication
import service.RootService

import java.util.Timer
import java.util.TimerTask

/**
 * Represents the main application for the SoPra board game.
 * The application initializes the [RootService] and displays the scenes.
 */

class SchiebePokerApplication : BoardGameApplication("SoPra Game") {

    /**
     * The root service instance. This is used to call service methods and access the entity layer.
     */
    val rootService: RootService = RootService()


    /**
     * The main game scene displayed in the application.
     */

    private val mainNewGameMenuScene    = NewGameMenuScene(rootService)
    private val handoffScene     = IntermissionScene(rootService)
    private val tableScene       = SchiebePokerGameScene(rootService)
    private val scoreBoardScene  = ScoreboardScene(rootService)

    // Index Spieler der gerade muss seine Karten vorshau sehen
    private var previewSpielerIdx = 0

    private var previewIsDone = false


    /**
     * Initializes the application by displaying the Scenes.
     */


    init {
        // beim Service registrieren da können Refreshes ankommen
        rootService.addRefreshable(tableScene)
        rootService.addRefreshable(scoreBoardScene)

        // Wenn Spiel fertig ist dann score anzeigen
        rootService.addRefreshable(object : service.Refreshable {
            override fun refreshAfterTurnEnd() {
                val game = rootService.currentGame ?: return
                showNextPlayerHandoff(game.currentPlayer)  // ← NEU
            }

                override fun refreshAfterGameEnd() {
                    scoreBoardScene.updateResults()
                    showMenuScene(scoreBoardScene)
                }
        })

        // Hauptmenü: Spiel starten
        mainNewGameMenuScene.startButton.onMouseClicked = {
            mainNewGameMenuScene.startGame()
            previewSpielerIdx = 0
            previewIsDone = false
            tableScene.updateView()
            hideMenuScene()
            showGameScene(tableScene)
            handoffScene.updatePlayerName(
                rootService.currentGame?.players?.getOrNull(0)?.name ?: "Spieler 1")
            showMenuScene(handoffScene)
        }

        // Übergabe-Bildschirm: wenn Spieler bereit dann wird verdeckte Karten kurz zeigen
        handoffScene.readyButton.onMouseClicked = onMouseClicked@{
            val game = rootService.currentGame ?: return@onMouseClicked

            if (!previewIsDone) {
                // Vorschau-Phase: verdeckte Karten 5 Sek zeigen
                tableScene.showHiddenCardsFor(previewSpielerIdx)
                hideMenuScene()
                showGameScene(tableScene)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        previewSpielerIdx++
                        if (previewSpielerIdx < game.players.size) {
                            tableScene.updateView()
                            showNextPlayerHandoff(previewSpielerIdx)
                        } else {
                            previewSpielerIdx = 0
                            previewIsDone = true
                            tableScene.updateView()
                            hideMenuScene()
                            showGameScene(tableScene)
                        }
                    }
                }, 5000)

            } else {
                // Normaler Zugwechsel: direkt weiterspielen, kein Timer!
                hideMenuScene()
            }
        }

        // Einzelne Karte tauschen wenn speielr gewählt ein hand karte und mitte karte
        tableScene.btnSwapOne.onMouseClicked = {
            tableScene.startSwapOneMode()
            tableScene.onSwapOneReady = { myCard, tableCard ->
                rootService.playerActionService.swapOneCard(myCard, tableCard)
            }
        }

        // Alle drei Karten tauschen
        tableScene.btnSwapAll.onMouseClicked = {
            rootService.playerActionService.swapAllCards()
        }

        // nix  tauschen
        tableScene.btnSwapNone.onMouseClicked = {
            rootService.playerActionService.swapNothing()
        }

        // Mittelkarten nach links schieben
        tableScene.btnShiftLeft.onMouseClicked = {
            rootService.playerActionService.pushLeft()
        }

        // Mittelkarten nach rechts schieben
        tableScene.btnShiftRight.onMouseClicked = {
            rootService.playerActionService.pushRight()
        }


        //  neues Spiel starten
        scoreBoardScene.newGameButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(mainNewGameMenuScene)
        }

        // spiel schliessen
        scoreBoardScene.exitButton.onMouseClicked = {
            System.exit(0)
        }

        showMenuScene(mainNewGameMenuScene)
        show()
    }

    /**
     * Setzt `previewIdx` auf den nächsten Spieler,
     * also aktualisiert den Namen im Übergabe-Bildschirm und zeigt ihn an
     *
     * @param idx Index des Spielers der als nächstes seine Karten sehen soll
     */
    private fun showNextPlayerHandoff(idx: Int) {
        val game = rootService.currentGame ?: return
        previewSpielerIdx = idx
        handoffScene.updatePlayerName(game.players[idx].name)
        showMenuScene(handoffScene)
    }
}