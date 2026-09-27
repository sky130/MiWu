package miwu.miot.utils

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking

class CoroutineTest {
    @Test
    fun runCatchingSuspendRethrowsCancellation() {
        runBlocking {
            assertFailsWith<CancellationException> {
                runCatchingSuspend<Unit> { throw CancellationException("cancelled") }
            }
        }
    }

    @Test
    fun receiverRunCatchingSuspendRethrowsCancellation() {
        runBlocking {
            assertFailsWith<CancellationException> {
                Unit.runCatchingSuspend<Unit, Unit> { throw CancellationException("cancelled") }
            }
        }
    }
}
