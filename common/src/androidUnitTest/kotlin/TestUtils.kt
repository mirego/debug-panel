import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.fail
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

internal fun runTestWithPendingCoroutines(body: suspend TestScope.() -> Unit) {
    var testCompleted = false
    try {
        runTest {
            body()
            testCompleted = true
            cancel()
        }
    } catch (e: Throwable) {
        if (!testCompleted) {
            fail(e.message)
        }
    }
}

internal val context: Context
    get() = InstrumentationRegistry.getInstrumentation().targetContext
