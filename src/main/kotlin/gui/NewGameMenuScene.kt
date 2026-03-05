package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * Die MenuScene zeigt Hauptmenü des Spiels.
 * Hier können Spieler Namen eingeben, Rundenzahl wählen und das Spiel starten.
 */
class NewGameMenuScene(private val rootService: RootService) :
    MenuScene(1920, 1080, background = ImageVisual("Background.png") ) {

    private val title = Label(
        width = 400, height = 150,
        posX = 732, posY = 10,
        text = "Schiebe-Poker",
        font = Font(size = 60, color = Color(255, 255, 255))
    )


    private val addPlayersButton = Button(
        width = 220, height = 55,
        posX = 450, posY = 700,
        text = "Add Players",
        visual = ColorVisual(Color(240, 230, 180))
    ).apply {
        font = Font(size = 32, color = Color.BLACK, fontWeight = Font.FontWeight.BOLD)
    }

    private val roundButton = Button(
        width = 220, height = 55,
        posX = 1230, posY = 700,
        text = "Round",
        visual = ColorVisual(Color(240, 230, 180))
    ).apply {
        font = Font(size = 32, color = Color.BLACK, fontWeight = Font.FontWeight.BOLD)
    }

    private val playerNumber = Label(
        width = 200, height = 40,
        posX = 50, posY = 150,
        text = "Player number",
        font = Font(size = 28, color = Color(255, 255, 255))
    )


    private val twoPlayersButton = Button(
        width = 60, height = 40,
        posX = 50, posY = 200,
        text = "2",
        visual = ColorVisual(Color(255, 255, 255))
    )

    private val threePlayersButton = Button(
        width = 60, height = 40,
        posX = 120, posY = 200,
        text = "3",
        visual = ColorVisual(Color(255, 200, 0))
    )

    private val fourPlayersButton = Button(
        width = 60, height = 40,
        posX = 190, posY = 200,
        text = "4",
        visual = ColorVisual(Color(255, 200, 0))
    )

    private val player1Input = TextField(
        width = 200, height = 40,
        posX = 50, posY = 280,
        prompt = "Player 1"
    )

    private val player2Input = TextField(
        width = 200, height = 40,
        posX = 50, posY = 330,
        prompt = "Player 2"
    )

    private val player3Input = TextField(
        width = 200, height = 40,
        posX = 50, posY = 380,
        prompt = "Player 3"
    )

    private val player4Input = TextField(
        width = 200, height = 40,
        posX = 50, posY = 430,
        prompt = "Player 4"
    )

    private val roundNumber = Label(
        width = 200, height = 40,
        posX = 40, posY = 180,
        text = "Round Nummer",
        font = Font(size = 28, color = Color(255, 255, 255))
    )

    private var selectedRound = 2   //automatish 2 wenn nicht gewählt

    private val roundButtons: List<Button> = (2..7).map { round ->
        Button(
            width = 60, height = 50,
            posX = 75 + ((round - 2) % 2) * 70, //round-2 weil round fangt mit 2,round2 ist idx 0,%2 für nebeneinander
            posY = 280 + ((round - 2) / 2) * 60, // hier / da element pro zeile
            text = "$round",
            visual = if (round == selectedRound)
                ColorVisual(Color(255, 200, 0)) // gelb für gewählte runde
            else
                ColorVisual(Color(255, 255, 255))
        ).apply {
            onMouseClicked = {
                selectedRound = round
                updateRoundButtons()
            }
        }
    }

    private val cancelButton = Button(
        width = 120, height = 45,
        posX = 30, posY = 520,
        text = "CANCEL",
        visual = ColorVisual(Color(211, 211, 211))
    )

    private val saveButton = Button(
        width = 120, height = 45,
        posX = 160, posY = 520,
        text = "SAVE",
        visual = ColorVisual(Color(255, 200, 0))
    )

    val startButton = Button(
        width = 200, height = 60,
        posX = 860, posY = 790,
        text = "Start",
        visual = ColorVisual(Color(220, 80, 80))
    ).apply {
        font = Font(size = 32, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    }

    val endButton = Button(
        width = 120, height = 50,
        posX = 900, posY = 860,
        text = "End",
        visual = ColorVisual(Color(211, 211, 211))
    ).apply {
        font = Font(size = 32, color = Color.BLACK, fontWeight = Font.FontWeight.BOLD)
    }



    private var selectedPlayerCount = 4 //automatish 4 player wenn nix wählen

    init {
        addComponents(
            title,
            playerNumber,
            twoPlayersButton, threePlayersButton, fourPlayersButton,
            player1Input, player2Input, player3Input, player4Input,
            roundNumber,
            cancelButton, saveButton,
            startButton, endButton,
            addPlayersButton, roundButton
        )
        roundButtons.forEach { addComponents(it) }

        // Alles verstecken am Anfang
        playerNumber.isVisible = false
        twoPlayersButton.isVisible = false
        threePlayersButton.isVisible = false
        fourPlayersButton.isVisible = false
        player1Input.isVisible = false
        player2Input.isVisible = false
        player3Input.isVisible = false
        player4Input.isVisible = false
        cancelButton.isVisible = false
        saveButton.isVisible = false
        roundNumber.isVisible = false
        roundButtons.forEach { it.isVisible = false }

        twoPlayersButton.onMouseClicked = { setPlayerCount(2) }
        threePlayersButton.onMouseClicked = { setPlayerCount(3) }
        fourPlayersButton.onMouseClicked = { setPlayerCount(4) }

        addPlayersButton.onMouseClicked = { showPlayerPanel() }
        roundButton.onMouseClicked = { showRoundPanel() }

        endButton.onMouseClicked = { System.exit(0) }

        cancelButton.onMouseClicked = { hideAllPanels() }
        saveButton.onMouseClicked = { hideAllPanels() }


        setPlayerCount(4)
        updateRoundButtons()
    }

    /**
     * Zeigt Spieler-Panel und versteckt Runden-Panel.
     */
    private fun showPlayerPanel() {
        playerNumber.isVisible = true
        twoPlayersButton.isVisible = true
        threePlayersButton.isVisible = true
        fourPlayersButton.isVisible = true
        player1Input.isVisible = true
        player2Input.isVisible = true
        player3Input.isVisible = selectedPlayerCount >= 3
        player4Input.isVisible = selectedPlayerCount >= 4
        cancelButton.isVisible = true
        saveButton.isVisible = true

        roundNumber.isVisible = false
        roundButtons.forEach { it.isVisible = false }
    }

    /**
     * Zeigt Runden-Panel und versteckt Spieler-Panel.
     */
    private fun showRoundPanel() {
        roundNumber.isVisible = true
        roundButtons.forEach { it.isVisible = true }
        cancelButton.isVisible = true
        saveButton.isVisible = true

        playerNumber.isVisible = false
        twoPlayersButton.isVisible = false
        threePlayersButton.isVisible = false
        fourPlayersButton.isVisible = false
        player1Input.isVisible = false
        player2Input.isVisible = false
        player3Input.isVisible = false
        player4Input.isVisible = false
    }

    /**
     * Versteckt alle Panels wenn Abbrechen geklickt wird.
     */
    private fun hideAllPanels() {
        playerNumber.isVisible = false
        twoPlayersButton.isVisible = false
        threePlayersButton.isVisible = false
        fourPlayersButton.isVisible = false
        player1Input.isVisible = false
        player2Input.isVisible = false
        player3Input.isVisible = false
        player4Input.isVisible = false
        cancelButton.isVisible = false
        saveButton.isVisible = false
        roundNumber.isVisible = false
        roundButtons.forEach { it.isVisible = false }
    }

    /**
     * Setzt die Spieleranzahl und aktualisiert UI.
     */
    private fun setPlayerCount(count: Int) {
        selectedPlayerCount = count

        twoPlayersButton.visual =
            if (count == 2) ColorVisual(Color(255, 200, 0)) // button gelb wenn gewählt
            else ColorVisual(Color(255, 255, 255))
        threePlayersButton.visual =
            if (count == 3) ColorVisual(Color(255, 200, 0))
             else ColorVisual(Color(255, 255, 255))
        fourPlayersButton.visual =
            if (count == 4) ColorVisual(Color(255, 200, 0))
            else ColorVisual(Color(255, 255, 255))

        player3Input.isDisabled = count < 3
        player4Input.isDisabled = count < 4


        player3Input.isVisible = (count >= 3) && playerNumber.isVisible
        player4Input.isVisible = (count >= 4) && playerNumber.isVisible
    }

    /**
     * Aktualisiert Runden-Buttons.
     */
    private fun updateRoundButtons() {
        roundButtons.forEach { button -> // für jede button mach so wie am nächste
            val round = button.text.toInt()
            button.visual = if (round == selectedRound)
                ColorVisual(Color(255, 200, 0))
            else
                ColorVisual(Color(255, 255, 255))
        }
    }

    /**
     * Startet das Spiel mit den eingegebenen Namen und Rundenzahl.
     */
    fun startGame() {
        val names = mutableListOf<String>()
        names.add(player1Input.text.trim().ifBlank { "Player 1" }) //trim für leerzeichen entfernen,
        names.add(player2Input.text.trim().ifBlank { "Player 2" }) //ifBlank wenn feld leer automatish Player heißt
        if (selectedPlayerCount >= 3)
            names.add(player3Input.text.trim().ifBlank { "Player 3" })
        if (selectedPlayerCount >= 4)
            names.add(player4Input.text.trim().ifBlank { "Player 4" })

        rootService.gameService.createGame(names, selectedRound)
    }
}