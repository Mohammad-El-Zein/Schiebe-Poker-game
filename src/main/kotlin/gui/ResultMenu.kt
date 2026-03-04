package gui

import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * Zeigt am Ende des Spiels die Rangliste aller Spieler mit ihrer
 * besten Kartenkombination und der erreichten Platzierung.
 *
 *
 */
class ResultMenu(private val rootService: RootService) :
    MenuScene(1920, 1080, background = ColorVisual(Color(34, 100, 34))),
    Refreshable {

    // Titel oben
    private val headline = Label(
        width = 600, height = 100,
        posX = 660, posY = 80,
        text = "Score",
        font = Font(size = 60, color = Color(255, 215, 0))
    )


    // Zeilen mit Spielername und Handbezeichnung
    private val playerResultRows = (0..3).map { i ->
        Label(
            width = 800, height = 70,
            posX = 560, posY = 220 + i * 120,
            text = "",
            font = Font(size = 36, color = Color(255, 255, 255)),
            visual = ColorVisual(Color(50, 120, 50))
        )
    }


    // Platzierung neben den Ergebnisse zeillen
    private val positionBadges = (0..3).map { i ->
        Label(
            width = 100, height = 70,
            posX = 450, posY = 220 + i * 120,
            text = "${i + 1}.",
            font = Font(size = 40, color = Color(255, 255, 255))
        )
    }

    // Neues Spiel starten
    val newGameButton = Button(
        width = 300, height = 65,
        posX = 660, posY = 780,
        text = "New Game",
        visual = ColorVisual(Color(100, 180, 100))
    ).apply {
        font = Font(size = 32, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    }


    // spiel beenden
    val exitButton = Button(
        width = 300, height = 65,
        posX = 980, posY = 780,
        text = "Exit",
        visual = ColorVisual(Color(200, 80, 80))
    ).apply {
        font = Font(size = 32, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    }



    init {
        addComponents(headline, newGameButton, exitButton)
        playerResultRows.forEach { addComponents(it) }
        positionBadges.forEach { addComponents(it) }

        // Erst verstecken bis echte Ergebnisse da sind
        playerResultRows.forEach { it.isVisible = false }
        positionBadges.forEach { it.isVisible = false }
    }

    /**
     * Liest die letzte ergebnisse aus aktuellem Spiel und
     * zeigt jeden Spieler mit seiner Platzierung und Hand results an.
     * Wird automatisch über [refreshAfterGameEnd] aufgerufen.
     */
    fun updateResults() {
        val game = rootService.currentGame ?: return

        playerResultRows.forEach { it.isVisible = false }
        positionBadges.forEach { it.isVisible = false }

        val sorted = game.playerScores.sortedBy { it.second }

        sorted.forEachIndexed { rowIdx, (playerIdx, place, handName) ->
            val name = game.players[playerIdx].name

            playerResultRows[rowIdx].text = "  $name  —  $handName"
            playerResultRows[rowIdx].isVisible = true

            positionBadges[rowIdx].text = "$place."
            positionBadges[rowIdx].visual = when (place) {
                1    -> ColorVisual(Color(255, 215, 0))
                2    -> ColorVisual(Color(192, 192, 192))
                3    -> ColorVisual(Color(205, 127, 50))
                else -> ColorVisual(Color(150, 150, 150))
            }
            positionBadges[rowIdx].isVisible = true
        }
    }

    override fun refreshAfterGameEnd() {
        updateResults()
    }
}