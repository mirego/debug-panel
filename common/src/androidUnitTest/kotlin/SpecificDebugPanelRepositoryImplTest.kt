import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirego.debugpanel.DebugPanelPickerItem
import com.mirego.debugpanel.annotations.DebugPanel
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

@Suppress("unused")
@DebugPanel("Test")
data class TestConfig(
    val toggle: Boolean,
    val action: () -> Unit,
    var textField: String,
    val picker: List<DebugPanelPickerItem>
)

@RunWith(AndroidJUnit4::class)
class SpecificDebugPanelRepositoryImplTest {
    private val repository by lazy {
        TestDebugPanelRepositoryImpl()
    }

    @BeforeTest
    fun setup() {
        initializeSettingsForTesting(context)
    }

    @Test
    fun `given a repository expect default values to be returned`() = runTest {
        assertNull(repository.getToggle().first())
        assertEquals(false, repository.getCurrentToggleValue("toggle", false))

        assertNull(repository.getTextField().first())
        assertEquals("", repository.getCurrentTextFieldValue("textField", ""))

        assertNull(repository.getPicker().first())
        assertNull(repository.getCurrentPickerValue("picker"))
    }

    @Test
    fun `given a repository with updated values expect the values to be returned`() = runTest {
        val toggleItemViewData: DebugPanelItemViewData.Toggle = mockk { every { identifier } returns "toggle" }
        val textFieldItemViewData: DebugPanelItemViewData.TextField = mockk { every { identifier } returns "textField" }
        val pickerItemViewData: DebugPanelItemViewData.Picker = mockk { every { identifier } returns "picker" }

        repository.onToggleUpdated(toggleItemViewData, true)
        repository.onTextFieldUpdated(textFieldItemViewData, "newText")
        repository.onPickerUpdated(pickerItemViewData, "item1")

        advanceUntilIdle()

        assertTrue(repository.getToggle().first() == true)
        assertEquals(true, repository.getCurrentToggleValue("toggle", false))

        assertTrue(repository.getTextField().first() == "newText")
        assertEquals("newText", repository.getCurrentTextFieldValue("textField", ""))

        assertTrue(repository.getPicker().first() == "item1")
        assertEquals("item1", repository.getCurrentPickerValue("picker"))
    }
}
