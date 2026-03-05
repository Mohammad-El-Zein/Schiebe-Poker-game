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
 * Zeigt am Ende des Spiels die Rangliste von alle Spieler mit ihre
 * besten Kartenkombination und erreichte Platzierung.
 *
 *
 */
class ScoreboardScene(private val rootService: RootService) :
    MenuScene(1920, 1080, background = ColorVisual(Color(34, 100, 34))),
    Refreshable {

    private val cardLoader = CardImageLoader()
    private val cardW = 60
    private val cardH = 90


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
            width = 620, height = 90,
            posX = 310, posY = 220 + i * 120,
            text = "",
            font = Font(size = 36, color = Color(255, 255, 255)),
            visual = ColorVisual(Color(50, 120, 50))
        )
    }


    // Platzierung neben den Ergebnisse zeillen
    private val positionBadges = (0..3).map { i ->
        Label(
            width = 100, height = 90,
            posX = 200, posY = 220 + i * 120,
            text = "${i + 1}.",
            font = Font(size = 40, color = Color(255, 255, 255))
        )
    }

    // 4 Spieler × 5 Karten rechts neben dem Namen
    private val resultCards: List<List<Label>> = (0..3).map { row -> // 4 reihen also 4 zeilen
        (0..4).map { column ->       //5 karten für jede zeile also 5x4 = 20 karten
            Label(
                width = cardW, height = cardH,
                posX = 950 + column * (cardW + 8),
                posY = 220 + row * 120,
                visual = cardLoader.blankImage
            )
        }
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
        resultCards.forEach { row -> row.forEach { addComponents(it) } }

        // Erst verstecken ,nur bei SCores sind gezeigt
        playerResultRows.forEach { it.isVisible = false }
        positionBadges.forEach { it.isVisible = false }
        resultCards.forEach { row -> row.forEach { it.isVisible = false } }

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
        resultCards.forEach { row -> row.forEach { it.isVisible = false } }

        val sorted = game.playerScores.sortedBy { it.second } //aus playerScores:player index, place, handName
                                                          //sortedBy it.second bedeutet by place da place ist second

        sorted.forEachIndexed { rowIndex, (playerIdx, place, handName) ->
            val name = game.players[playerIdx].name
            val player = game.players[playerIdx]


            playerResultRows[rowIndex].text = "  $name  —  $handName"
            playerResultRows[rowIndex].isVisible = true

            positionBadges[rowIndex].text = "$place."
            positionBadges[rowIndex].visual = when (place) {
                1    -> ColorVisual(Color(255, 215, 0)) //gold
                2    -> ColorVisual(Color(192, 192, 192))//silver
                3    -> ColorVisual(Color(205, 127, 50))
                else -> ColorVisual(Color(150, 150, 150))
            }
            positionBadges[rowIndex].isVisible = true

            val allCards = player.hiddenCards + player.openCards
            allCards.forEachIndexed { cardIdx, card ->    //wird jede spieler seine 5 karten auch gezeigt in score
                resultCards[rowIndex][cardIdx].visual =
                    cardLoader.frontImageFor(card.suit, card.value)
                resultCards[rowIndex][cardIdx].isVisible = true
            }
        }
    }

    override fun refreshAfterGameEnd() {
        updateResults()
    }
}