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
 * die Aktions-Buttons für den aktuellen Zug.
 *
 *  wird aktualisiert sich automatisch nach jeder Aktion über [Refreshable]-Interface.
 *
 */
class GameScene(private val rootService: RootService) :
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

    // Einzelne Zeilen im Protokoll (maximal 5 sichtbare Einträge)
    private val logEntries = (0..4).map { i ->
        Label(
            width = 690, height = 45,
            posX = 585, posY = 55 + i * 48,
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

    private val nameLabels = listOf(             // Name auf jeden Spieler
        Label(width = 160, height = 40,
            posX = 50,   posY = 200,
            text = "Spieler 1",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180))),
        Label(width = 160, height = 40,
            posX = 1710, posY = 200,
            text = "Spieler 2",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180))),
        Label(width = 160, height = 40,
            posX = 1710, posY = 800,
            text = "Spieler 3",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180))),
        Label(width = 160, height = 40,
            posX = 50,   posY = 800,
            text = "Spieler 4",
            font = Font(size = 18, color = Color(0, 0, 0)),
            visual = ColorVisual(Color(240, 230, 180)))
    )

    // Startpositionen der 5 Karten jeder Spieler
    private val handCardPositions = listOf(
        Pair(50,   60),
        Pair(1420, 60),
        Pair(1420, 860),
        Pair(50,   860)
    )

    // Alle Karten aller Spieler
    private val playerCards: List<List<CardView>> = (0..3).map { idx ->
        val (x, y) = handCardPositions[idx]
        (0..4).map { pos ->
            CardView(
                width = cardW, height = cardH,
                posX = x + pos * (cardW + 10),
                posY = y,
                front = cardLoader.blankImage,
                back = cardLoader.backImage
            )
        }
    }

    //  drei mitte offenen Karten
    private val tableCards = (0..2).map { i ->
        CardView(
            width = cardW + 20, height = cardH + 30,
            posX = 760 + i * (cardW + 30),
            posY = 450,
            front = cardLoader.blankImage,
            back = cardLoader.backImage
        )
    }

    // Nachziehstapel rechts
    private val drawPile = CardView(
        width = cardW + 20, height = cardH + 30,
        posX = 1150, posY = 450,
        front = cardLoader.blankImage,
        back = cardLoader.backImage
    )

    // Ablagestapel links
    private val discardPile = CardView(
        width = cardW + 20, height = cardH + 30,
        posX = 610, posY = 450,
        front = cardLoader.blankImage,
        back = cardLoader.backImage
    )


    // Aktions-Buttons
    val btnSwapOne    = Button(width = 180, height = 55, posX = 760, posY = 790,
        text = "Swap One",    visual = ColorVisual(Color(255, 200, 0)))
    val btnSwapAll    = Button(width = 180, height = 55, posX = 760, posY = 855,
        text = "Swap All",    visual = ColorVisual(Color(200, 230, 200)))
    val btnSwapNone   = Button(width = 180, height = 55, posX = 760, posY = 920,
        text = "Swap None",      visual = ColorVisual(Color(200, 230, 200)))
    val btnShiftLeft  = Button(width = 180, height = 55, posX = 960, posY = 790,
        text = "Shift Left",  visual = ColorVisual(Color(200, 230, 200)))
    val btnShiftRight = Button(width = 180, height = 55, posX = 960, posY = 855,
        text = "Shift Right", visual = ColorVisual(Color(200, 230, 200)))
    val btnConfirm    = Button(width = 180, height = 55, posX = 960, posY = 920,
        text = "Next turn", visual = ColorVisual(Color(180, 180, 180)))

    // Zustand für den Einzelkarten-Tausch
    private var swapModeActive = false
    private var chosenCardIdx = -1  // nnoch keine karte

    /**
     * Callback der aufgerufen wird sobald der Spieler beide Karten für den
     * Einzeltausch ausgewählt hat. Liefert (eigeneKarte, Mittelkarte).
     */
    var onSwapOneReady: ((playerCardIdx: Int, centerCardIdx: Int) -> Unit)? = null // ?=null kann niemand  schongesetzt

    init {
        addComponents(logBox, roundInfo, drawPile, discardPile,
            btnSwapOne, btnSwapAll, btnSwapNone,
            btnShiftLeft, btnShiftRight, btnConfirm)
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

        // Spieler 3 und 4 erst mal ausblenden
        nameLabels[2].isVisible = false
        nameLabels[3].isVisible = false
        playerCards[2].forEach { it.isVisible = false }
        playerCards[3].forEach { it.isVisible = false }
    }

    // Refreshable-Callbacks
    override fun refreshAfterAction()      { updateView() }
    override fun refreshAfterTurnStart()   { updateView() }
    override fun refreshAfterTurnEnd()     { updateView() }
    override fun refreshAfterRefillStack() { updateView() }
    override fun refreshAfterGameEnd()     {}

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

        for (j in 0..2) {
            val viewIdx = j + 2  //da offenen Karten am unsere gui an Idx 2,3,4 liegen.
            val backendIdx = j  // normal index in spiellogik, also normal idx 0,1,2
            playerCards[who][viewIdx].onMouseClicked = {
                chosenCardIdx = backendIdx
                markChosenCard(who, viewIdx)
            }
        }

        for (k in 0..2) {
            val viewIndex = k
            tableCards[k].onMouseClicked = {
                if (chosenCardIdx != -1) {
                    // also invoke hier ist ruf dieses funktion wenn nicht null ist
                    onSwapOneReady?.invoke(chosenCardIdx, viewIndex)
                    cancelSwapOneMode()

                    //Wenn onSwapOneReady gesetzt ist, rufe sie mit den Argumenten chosenCardIdx
                    // und viewIndex auf
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

        val game = rootService.currentGame ?: return
        val who = game.currentPlayer

        for (j in 2..4) {
            playerCards[who][j].scaleX = 1.0   // Karten Zoom zurücksetzen
            playerCards[who][j].scaleY = 1.0
        }
        tableCards.forEach { it.onMouseClicked = null } //player kann nicht klick in Mitte karten

        updateView()
    }

    /**
     * Wird die beiden verdeckten Karten eines Spielers offen , das nur in der Vorschau-Phase benutzt,
     * damit jeder Spieler kurz seine Startkarten sehen kann.
     *
     * @param playerIdx Index des Spielers dessen Karten aufgedeckt werden
     */
    fun showHiddenCardsFor(playerIdx: Int) {
        val game = rootService.currentGame ?: return
        val player = game.players[playerIdx]
        val cards = playerCards[playerIdx]

        player.hiddenCards.forEachIndexed { i, card ->
            cards[i].frontVisual = cardLoader.frontImageFor(card.suit, card.value) // muss richtige Kartenbild zeigen

            cards[i].showFront()     // Karte umdrehen also vorderseite statt ruckseite
            cards[i].scaleX = 1.15 //wird bischen großer sein um player wissen das sind die verdeckte karten
            cards[i].scaleY = 1.15
        }
    }

    /**
     * Aktualisiert die gesamte Spielansicht anhand des aktuellen Spielzustands.
     * Wird nach jeder Aktion, nach jedem Zugwechsel und beim Rundenstart aufgerufen.
     */
    fun updateView() {
        val game = rootService.currentGame ?: return

        roundInfo.text = "Runde ${game.currentRound} / ${game.gameRound}" //7aktuelle runde gezeigt

        // Spieler Bereich aktualisieren, zb wnn 2 spieler, wird die SpieleIdx 2 und 3 nicht gezeigt
        for (i in 0..3) {
            val isInGame = i < game.players.size
            nameLabels[i].isVisible = isInGame
            playerCards[i].forEach { it.isVisible = isInGame }

            if (isInGame) {
                val pIdx = i
                val player = game.players[pIdx]
                nameLabels[pIdx].text = player.name

                // Aktiver Spieler muss blau sein , alle anderen beige
                nameLabels[pIdx].visual = if (pIdx == game.currentPlayer)
                    ColorVisual(Color(100, 180, 255))
                else
                    ColorVisual(Color(240, 230, 180))

                // Verdeckte Karten sind nochmal normal und verdeckt
                playerCards[pIdx][0].scaleX = 1.0
                playerCards[pIdx][0].scaleY = 1.0
                playerCards[pIdx][0].showBack()

                playerCards[pIdx][1].scaleX = 1.0
                playerCards[pIdx][1].scaleY = 1.0
                playerCards[pIdx][1].showBack()

                // Offene Karten für jede spiele anzeigen, ist j+2 weil erste 2 sind verdeckt
                player.openCards.forEachIndexed { j, card ->
                    playerCards[pIdx][j + 2].frontVisual =
                        cardLoader.frontImageFor(card.suit, card.value)
                    playerCards[pIdx][j + 2].showFront()
                }

                // nur active spieler kann sein verdeckte Karten klicken und sehen
                if (pIdx == game.currentPlayer) {
                    playerCards[pIdx][0].onMouseClicked = {
                        val card = game.players[pIdx].hiddenCards[0]
                        playerCards[pIdx][0].frontVisual =
                            cardLoader.frontImageFor(card.suit, card.value)
                        if (playerCards[pIdx][0].currentSide == CardView.CardSide.FRONT)
                            playerCards[pIdx][0].showBack()
                        else
                            playerCards[pIdx][0].showFront()
                    }
                    playerCards[pIdx][1].onMouseClicked = {
                        val card = game.players[pIdx].hiddenCards[1]
                        playerCards[pIdx][1].frontVisual =
                            cardLoader.frontImageFor(card.suit, card.value)
                        if (playerCards[pIdx][1].currentSide == CardView.CardSide.FRONT)
                            playerCards[pIdx][1].showBack()
                        else
                            playerCards[pIdx][1].showFront()
                    }
                } else {
                    playerCards[pIdx][0].onMouseClicked = null
                    playerCards[pIdx][1].onMouseClicked = null
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
            label.text = recent.getOrElse(i) { "" }
        }
    }
}