import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.initializeSettingsForTesting
import com.mirego.debugpanel.repository.TestDebugPanelRepositoryImpl
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.mirego.debugpanel.usecase.TestDebugPanelUseCaseImpl
import com.mirego.debugpanel.viewmodel.DebugPanelItemViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.runner.RunWith

@DebugPanel("Test")
data class TestConfig(
    @DisplayName("toggle") val toggleId: Boolean,
    @DisplayName("action") val actionId: () -> Unit
)

@RunWith(AndroidJUnit4::class)
class SpecificDebugPanelRepositoryImplTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val repository by lazy {
        TestDebugPanelRepositoryImpl()
    }

    @BeforeTest
    fun setup() {
        initializeSettingsForTesting(context)
    }

    @Test
    fun test() = runTestAllowUncompletedCoroutines {
        assertNull(repository.getToggleId().first())

        repository.onToggleUpdated(mockk<DebugPanelItemViewData.Toggle> { every { identifier } returns "toggleId" }, true)

        advanceUntilIdle()

        assertTrue(repository.getToggleId().first() == true)
        assertTrue(repository.getCurrentToggleValue("toggleId", false))
    }
}
