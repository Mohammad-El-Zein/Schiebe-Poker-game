package gui

import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 *  Hier sieht man alle Karten, die Spieler und
 * die Aktions-Buttons für die aktuellen Zug.
 *
 *  wird aktualisiert sich automatisch nach jeder Aktion über [Refreshable]-Interface.
 *
 */
class SchiebePokerGameScene(private val rootService: RootService) :
    BoardGameScene(2020, 1080, background = ColorVisual(Color(34, 100, 34))),
    Refreshable {

    private val cardLoader = CardImageLoader()
    private val cardW = 80  //width
    private val cardH = 120  //height


    private val logBox = Label(      //log background
        width = 750, height = 310,
        posX = 580, posY = 45,
        visual = ColorVisual(Color(80, 80, 80))
    )

    private val logEntries = (0..4).map { i ->  // jede label hat 5 max log entries.
        Label(
            width = 690, height = 45,
            posX = 585, posY = 55 + i * 48, //zb für erste zahl: 55 + 0*48 , zweite: 55 + 1*48 , gleiche abstand
            text = "",
            font = Font(size = 14, color = Color(255, 255, 255))
        )
    }

    private val roundInfo = Label(          // aktuelle Runde
        width = 200, height = 40,
        posX = 820, posY = 10,
        text = "Runde 1 / 3",
        font = Font(size = 20, color = Color(255, 255, 255))
    )

    private val nameLabels = listOf(  // Name auf jeden Spieler
        Label(width = 160, height = 40,
            posX = 50,   posY = 200,
            text = "Spieler 1",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180))),
        Label(width = 160, height = 40,
            posX = 1710,
            posY = 200,
            text = "Spieler 2",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180))),
        Label(width = 160, height = 40,
            posX = 1710,
            posY = 800,
            text = "Spieler 3",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180))),
        Label(width = 160, height = 40,
            posX = 50,
            posY = 800,
            text = "Spieler 4",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180)))
    )

    // Startpositionen der 5 hand Karten jeder Spieler
    private val handCardPositions = listOf(
        Pair(50,   60), // also (x,y)
        Pair(1420, 60),
        Pair(1420, 860),
        Pair(50,   860)
    )

    // Alle Karten aller Spieler
    private val playerCards: List<List<CardView>> = (0..3).map { Index ->
        val (x, y) = handCardPositions[Index] // pair(x,y) auf handCardPostions nehmen
        (0..4).map { position ->  // für jede karte auf die 5 karten
            val extraGap = if (position >= 2) 30 else 0
            CardView(
                width = cardW, height = cardH,
                posX = x + position * (cardW + 10) + extraGap, // bedeutet abstand zwischen jeder karte ist 10
                posY = y,
                front = cardLoader.blankImage,
                back = cardLoader.backImage
            )
        }

    }


    //  drei mitte offenen Karten
    private val tableCards = (0..2).map { i ->
        CardView(
            width = cardW + 20,
            height = cardH + 30,
            posX = 760 + i * (cardW + 30),
            posY = 450,
            front = cardLoader.blankImage,
            back = cardLoader.backImage
        )
    }

    // Nachziehstapel rechts legen
    private val drawPile = CardView(
        width = cardW + 20, height = cardH + 30,
        posX = 1150, posY = 450,
        front = cardLoader.blankImage,
        back = cardLoader.backImage
    )

    // Ablagestapel links legen
    private val discardPile = CardView(
        width = cardW + 20, height = cardH + 30,
        posX = 610, posY = 450,
        front = cardLoader.blankImage,
        back = cardLoader.backImage
    )


    // aktion bttn
    val btnSwapOne    = Button(width = 180, height = 55, posX = 760, posY = 790,
        text = "Swap One",  visual = ColorVisual(Color(200, 230, 200)))
    val btnSwapAll    = Button(width = 180, height = 55, posX = 760, posY = 855,
        text = "Swap All",    visual = ColorVisual(Color(200, 230, 200)))
    val btnSwapNone   = Button(width = 180, height = 55, posX = 760, posY = 920,
        text = "Swap None",      visual = ColorVisual(Color(200, 230, 200)))
    val btnShiftLeft  = Button(width = 180, height = 55, posX = 960, posY = 790,
        text = "Shift Left",  visual = ColorVisual(Color(200, 230, 200)))
    val btnShiftRight = Button(width = 180, height = 55, posX = 960, posY = 855,
        text = "Shift Right", visual = ColorVisual(Color(200, 230, 200)))


    // Zustand für den Einzelkarten Tausch
    private var swapModeActive = false
    private var chosenCardIdx = -1  // nnoch keine karte

    /**
     *  aufgerufen wird sobald Spieler beide Karten für den
     * Einzeltausch ausgewählt hat, also seone eigeneKarte und  Mittelkarte).
     */
    var onSwapOneReady: ((playerCardIdx: Int, centerCardIdx: Int) -> Unit)? = null // ?=null kann niemand  schongesetzt

    init {
        addComponents(logBox, roundInfo, drawPile, discardPile,
            btnSwapOne, btnSwapAll,
                    btnSwapNone,
            btnShiftLeft, btnShiftRight)
        logEntries.forEach { addComponents(it) }
        nameLabels.forEach { addComponents(it) }
        playerCards.forEach { cards -> cards.forEach { addComponents(it) } }
        tableCards.forEach { addComponents(it) }

        // Alle am start was verdekct soll sein
        playerCards.forEach { cards -> cards.forEach { it.showBack() } }
        tableCards.forEach { it.showBack() }
        drawPile.showBack()

        // Ablagestapel startet leer (weiße Karte)
        discardPile.frontVisual = cardLoader.blankImage
        discardPile.showFront()

        // Spieler 3 und 4 erst mal nicht gezeigt ,nur wenn sind hinzugefügt
        nameLabels[2].isVisible = false
        nameLabels[3].isVisible = false
        playerCards[2].forEach { it.isVisible = false }
        playerCards[3].forEach { it.isVisible = false }
    }

    // Refreshable-Callbacks
    override fun refreshAfterAction() {
        val game = rootService.currentGame ?: return
        if (game.countAction == 2) {
            // Automatisch Zug beenden wenn 2 Aktionen gemacht
            rootService.gameService.endTurn()
        } else {
            updateView()
        }
    }
    override fun refreshAfterTurnStart()   { updateView() }
    override fun refreshAfterTurnEnd()     { updateView() }
    override fun refreshAfterRefillStack() { updateView() }
    override fun refreshAfterGameEnd()     {}
    override fun refreshAfterGameStart()    {}

    /**
     * In der Einzelkarten-Tausch-Modus, muss  Spieler  zuerst eine
     * seiner offenen Karten anklicken, danach eine Karte aus der Mitte anklicken.
     * wenn beide gewählt sind, wird [onSwapOneReady] aufgerufen.
     */
    fun startSwapOneMode() {
        swapModeActive = true
        chosenCardIdx = -1

        val game = rootService.currentGame ?: return
        val who = game.currentPlayer

        for (j in 0..2) {  //handkart
            val viewIdx = j + 2  //da offenen Karten am unsere gui an Idx 2,3,4 liegen, also ist was sehen in gui idx
            val backendIdx = j  // normal index in spiellogik, also normal idx 0,1,2
            playerCards[who][viewIdx].onMouseClicked = {
                chosenCardIdx = backendIdx
                markChosenCard(who, viewIdx)
            }
        }

        for (k in 0..2) {  //mittelkart
            val viewIndex = k
            tableCards[k].onMouseClicked = {
                if (chosenCardIdx != -1) {
                    // also invoke hier ist , ruf dieses funktion wenn nicht null ist
                    onSwapOneReady?.invoke(chosenCardIdx, viewIndex)
                    cancelSwapOneMode()


                }
            }
        }
    }

    /**
     * wenn spieler klickt auf ein karte, dieangeklickte Karte wird leichtes Vergrößern,
     * alle anderen offenen Karten des Spielers bleibt normal.
     *
     * @param playerIdx Index des Spielers
     * @param cardIdx   Index der Karte im Layout (2–4)
     */
    private fun markChosenCard(playerIdx: Int, cardIdx: Int) {
        for (j in 2..4) {
            playerCards[playerIdx][j].scaleX = 1.0
            playerCards[playerIdx][j].scaleY = 1.0
        }
        playerCards[playerIdx][cardIdx].scaleX = 1.15
        playerCards[playerIdx][cardIdx].scaleY = 1.15
    }

    /**
     * Nach tauschung, wird die Einzelkarten-Tausch-Modus entfernt un
     * d stellt den normalen Zustand der Karten wieder her
     * Also alles wird nochmal Normal gezeigt
     */
    private fun cancelSwapOneMode() {
        swapModeActive = false
        chosenCardIdx = -1

        for (pIdx in 0..3) {
            for (j in 2..4) {
                playerCards[pIdx][j].scaleX = 1.0
                playerCards[pIdx][j].scaleY = 1.0
            }
        }
        tableCards.forEach { it.onMouseClicked = null } //player kann nicht klick in Mitte karten

        updateView()
    }

    /**
    * Zeigt alle 5 Karten eines Spielers in der Vorschau-Phase.
    * Verdeckte Karten werden leicht vergrößert dargestellt.
    *
    * @param playerIdx Index des Spielers dessen Karten angezeigt werden
    */
    fun showAllCardsFor(playerIdx: Int) {
        val game = rootService.currentGame ?: return
        val player = game.players[playerIdx]
        val cards = playerCards[playerIdx]


        player.openCards.forEachIndexed { i, card ->
            cards[i + 2].frontVisual = cardLoader.frontImageFor(card.suit, card.value)
            cards[i + 2].showFront()
        }
    }



    /**
     * Aktualisiert die gesamte Spielansicht anhand von aktuelle Spielzustand.
     * Wird nach jeder Aktion, nach jedem Zugwechsel und beim Rundenstart aufgerufen.
     */
    fun updateView() {
        val game = rootService.currentGame ?: return

        roundInfo.text = "Runde ${game.currentRound} / ${game.gameRound}" //7aktuelle runde gezeigt

        // Spieler Bereich aktualisieren, zb wnn 2 spieler, wird die name label für SpieleIdx 2 und 3 nicht gezeigt
        for (i in 0..3) {
            val isInGame = i < game.players.size
            nameLabels[i].isVisible = isInGame
            playerCards[i].forEach { it.isVisible = isInGame }

            if (isInGame) {
                val playerIndx = i
                val player = game.players[playerIndx]
                nameLabels[playerIndx].text = player.name

                // Aktiver Spieler muss blau sein , alle anderen beige
                nameLabels[playerIndx].visual = if (playerIndx == game.currentPlayer)
                    ColorVisual(Color(100, 180, 255))
                else
                    ColorVisual(Color(240, 230, 180))


                // Offene Karten für jede spiele anzeigen, ist j+2 weil erste 2 sind verdeckt
                player.openCards.forEachIndexed { j, card ->
                    playerCards[playerIndx][j + 2].frontVisual =
                        cardLoader.frontImageFor(card.suit, card.value)
                    playerCards[playerIndx][j + 2].showFront()
                }

                if (playerIndx == game.currentPlayer) {
                    val card0 = player.hiddenCards[0]
                    val card1 = player.hiddenCards[1]
                    playerCards[playerIndx][0].frontVisual = cardLoader.frontImageFor(card0.suit, card0.value)
                    playerCards[playerIndx][0].showFront()
                    playerCards[playerIndx][1].frontVisual = cardLoader.frontImageFor(card1.suit, card1.value)
                    playerCards[playerIndx][1].showFront()
                } else {
                    playerCards[playerIndx][0].showBack()
                    playerCards[playerIndx][1].showBack()
                    playerCards[playerIndx][0].onMouseClicked = null
                    playerCards[playerIndx][1].onMouseClicked = null
                }
            }
        }
        // mitte karten aktualisiert und gezeigt
        game.centerCards.forEachIndexed { i, card ->
            tableCards[i].frontVisual = cardLoader.frontImageFor(card.suit, card.value)
            tableCards[i].showFront()
        }

        // Ablagestapel ist zuerst leer dann weiße Karte, sonst oberste Karte
        if (game.discardPile.isEmpty()) {
            discardPile.frontVisual = cardLoader.blankImage
            discardPile.showFront()
        } else {
            val top = game.discardPile.last()
            discardPile.frontVisual = cardLoader.frontImageFor(top.suit, top.value)
            discardPile.showFront()
        }
        // Nachziehstapel immer aufruckseite solange karten enthält
        if (game.drawPile.isNotEmpty()) {
            drawPile.isVisible = true
            drawPile.showBack()
        } else {
            drawPile.isVisible = false
        }

        // nimm letzte 5 Log message, und am oben bis unten sind die neuste message
        val recent = game.moveLog.takeLast(5).reversed()
        logEntries.forEachIndexed { i, label ->
            label.text = recent.getOrElse(i) { "" } // falls weniger als 5 dann leer text sertzen
        }
    }
}