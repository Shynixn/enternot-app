package at.jku.enternot

import android.content.Context
import android.content.SharedPreferences
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.service.ConfigurationServiceImpl
import org.junit.Assert
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class TConfigurationService {

    /**
     * Given
     *     No Previous stored configuration
     *  When
     *      getConfiguration is called
     *  Then
     *      a null configuration should be returned.
     */
    @Test
    fun getConfiguration_NoPreviousConfigurationInContext_ShouldReturnConfiguration() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val context = mock(Context::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)
        `when`(sharedPreferences.getString(Matchers.anyString(), Matchers.anyObject())).thenReturn(null)
        println(sharedPreferences)
        `when`(context.getSharedPreferences(Matchers.anyString(), Matchers.anyInt())).thenReturn(sharedPreferences)
        `when`(context.getString(Matchers.anyInt())).thenReturn("sample")

        // Act
        val currentValue = classUnderTest.getConfiguration(context)

        // Assert
        Assert.assertNull(currentValue)
    }

    companion object {
        fun createWithDependencies(): ConfigurationService {
            return ConfigurationServiceImpl()
        }
    }
}