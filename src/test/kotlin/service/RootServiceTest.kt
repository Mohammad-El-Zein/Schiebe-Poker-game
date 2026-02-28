package service

import kotlin.test.*

/**
 * Unit Test für [RootService].
 */

class RootServiceTest {

    private lateinit var rootService: RootService

    @BeforeTest
    fun setUp() {
        rootService = RootService()
    }

    /** vor die createGame muss die currentGame null seein*/
    @Test
    fun ` current game should be null before the start`() {
        assertNull(rootService.currentGame)
    }

    /**  nach  createGame muss currentGame nicht null sein.*/
    @Test
    fun `current game should be not null after the start`() {
        rootService.gameService.createGame(listOf("Mo", "Zein"), 5)
        assertNotNull(rootService.currentGame)
    }

    /** muss gameService und playerActionService initialisiert und nicht null sein.*/
    @Test
    fun `Services are installed`() {
        assertNotNull(rootService.gameService)
        assertNotNull(rootService.playerActionService)
    }

}