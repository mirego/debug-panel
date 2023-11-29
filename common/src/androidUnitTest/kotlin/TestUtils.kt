import com.mirego.debugpanel.service.DebugPanelSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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

fun mockSettings(): Pair<ObservableSettings, FlowSettings> {
    mockkObject(DebugPanelSettings)

    val flowSettings = mockk<FlowSettings>()
    val observableSettings = mockk<ObservableSettings>()

    every { DebugPanelSettings.observableSettings } returns observableSettings
    every { DebugPanelSettings.flowSettings } returns flowSettings

    return observableSettings to flowSettings
}
