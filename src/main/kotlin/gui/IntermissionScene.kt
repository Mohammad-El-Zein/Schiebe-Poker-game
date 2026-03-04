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
 * wird dieses scene zwischen den Zügen angezeigt, damit das Gerät an den nächsten
 * Spieler weitergegeben werden ohne dass jemand die Karten der anderen sieht.
 *
 * Der aktuelle Spieler bestätigt seine bereit mit drucken auf die button karte zeigen,
 * dann kann ihre 5 karten sehen, also wird verdeckte karten geoffnet auch
 *
 */
class IntermissionScene(private val rootService: RootService) :
    MenuScene(1920, 1080, background = ColorVisual(Color(50, 132, 50))),
    Refreshable {

    // Zeigt den Namen des Spielers der gerade dran ist
    private val currentPlayerLabel = Label(
        width = 700, height = 100,
        posX = 620, posY = 320,
        text = "Du bist dran, !",
        font = Font(size = 70, color = Color(0, 0, 0))
    )

    //  Geräteübergabe
    private val handover = Label(
        width = 600, height = 50,
        posX = 660, posY = 460,
        text = "Bitte übergib das Gerät",
        font = Font(size = 44, color = Color(0, 0, 0))
    )

    // Spieler drückt hier wenn er bereit ist seine Karten zu sehen
    val readyButton = Button(
        width = 600, height = 60,
        posX = 670, posY = 620,
        text = "Ich bin bereit, Karten anzeigen",
        visual = ColorVisual(Color(180, 230, 180))
    ).apply {
        font = Font(size = 32, color = Color.BLACK, fontWeight = Font.FontWeight.BOLD)
    }

    init {
        addComponents(currentPlayerLabel, handover, readyButton)
    }

    /**
     * Setzt den Namen des Spielers der gerade dran ist.
     *
     * @param name Name des aktuellen Spielers
     */
    fun updatePlayerName(name: String) {
        currentPlayerLabel.text = "Du bist dran, $name!"
    }
}