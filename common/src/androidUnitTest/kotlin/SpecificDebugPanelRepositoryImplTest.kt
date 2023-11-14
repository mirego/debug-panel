import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.initializeSettingsForTesting
import com.mirego.debugpanel.repository.TestDebugPanelRepositoryImpl
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith

@DebugPanel("Test")
data class TestConfig(
    val toggle: Boolean,
    val action: () -> Unit,
    var textField: String
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
    fun `test`() = runTest {
        assertNull(repository.getToggle().first())

        assertEquals(null, repository.getTextField().first())

        repository.onToggleUpdated(mockk<DebugPanelItemViewData.Toggle> { every { identifier } returns "toggle" }, true)
        repository.onTextFieldUpdated(mockk<DebugPanelItemViewData.TextField> { every { identifier } returns "textField" }, "newText")

        advanceUntilIdle()

        assertTrue(repository.getToggle().first() == true)
        assertEquals(false, repository.getCurrentToggleValue("toggle", false))

        assertTrue(repository.getTextField().first() == "newText")
        assertEquals("newText", repository.getCurrentTextFieldValue("textField", ""))
    }
}
