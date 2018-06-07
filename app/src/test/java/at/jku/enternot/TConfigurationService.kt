package at.jku.enternot

import android.content.Context
import android.content.SharedPreferences
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.entity.Configuration
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
    fun getConfiguration_NoPreviousConfigurationInContext_ShouldReturnNullConfiguration() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val context = mock(Context::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)

        `when`(sharedPreferences.getString(Matchers.anyString(), Matchers.anyObject())).thenReturn(null)
        `when`(context.getSharedPreferences(Matchers.anyString(), Matchers.anyInt())).thenReturn(sharedPreferences)
        `when`(context.getString(Matchers.anyInt())).thenReturn("sample")

        // Act
        val currentValue = classUnderTest.getConfiguration(context)

        // Assert
        Assert.assertNull(currentValue)
    }

    /**
     * Given
     *     Partially stored configuration
     *  When
     *      getConfiguration is called
     *  Then
     *      a null configuration should be returned.
     */
    @Test
    fun getConfiguration_PartialConfigurationInContext_ShouldReturnNullConfiguration() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val context = mock(Context::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)

        `when`(sharedPreferences.getString(Matchers.anyString(), Matchers.anyObject())).thenReturn(null)
        `when`(sharedPreferences.getString("hostname", null)).thenReturn("samplehostname.org")
        `when`(context.getSharedPreferences(Matchers.anyString(), Matchers.anyInt())).thenReturn(sharedPreferences)
        `when`(context.getString(Matchers.anyInt())).thenReturn("sample")

        // Act
        val currentValue = classUnderTest.getConfiguration(context)

        // Assert
        Assert.assertNull(currentValue)
    }

    /**
     * Given
     *     Full stored configuration
     *  When
     *      getConfiguration is called
     *  Then
     *      a full configuration  should be returned.
     */
    @Test
    fun getConfiguration_FullConfigurationInContext_ShouldReturnValidConfiguration() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val context = mock(Context::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)

        `when`(sharedPreferences.getString(Matchers.anyString(), Matchers.anyObject())).thenReturn(null)
        `when`(sharedPreferences.getString("hostname", null)).thenReturn("samplehostname.org")
        `when`(sharedPreferences.getString("username", null)).thenReturn("Bobder")
        `when`(sharedPreferences.getString("password", null)).thenReturn("Baumeister")
        `when`(context.getSharedPreferences(Matchers.anyString(), Matchers.anyInt())).thenReturn(sharedPreferences)
        `when`(context.getString(Matchers.anyInt())).thenReturn("sample")

        val expectedConfiguration = Configuration("samplehostname.org", "Bobder", "Baumeister")


        // Act
        val currentValue = classUnderTest.getConfiguration(context)

        // Assert
        Assert.assertNotNull(currentValue)
        Assert.assertEquals(expectedConfiguration.hostname, currentValue!!.hostname)
        Assert.assertEquals(expectedConfiguration.username, currentValue.username)
        Assert.assertEquals(expectedConfiguration.password, currentValue.password)
    }

    /**
     * Given
     *     Any Previous stored configuration or empty
     *  When
     *      saveConfiguration is called
     *  Then
     *      the values should be correctly set.
     */
    @Test
    fun saveConfiguration_NoPreviousConfigurationInContext_ShouldSetConfigurationValues() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val context = mock(Context::class.java)
        val configuration = Configuration("host", "super", "man")
        val sharedPreferences = mock(SharedPreferences::class.java)
        val sharedPreferencesEditor = mock(SharedPreferences.Editor::class.java)
        var currentValue: String? = null
        val expectedValue = "man"

        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        `when`(context.getString(Matchers.anyInt())).thenReturn("sample")
        `when`(context.getSharedPreferences(Matchers.anyString(), Matchers.anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferencesEditor.putString(Matchers.anyString(), Matchers.anyString())).then { p ->
            currentValue = p.getArgumentAt(1, String::class.java)
            sharedPreferencesEditor
        }

        // Act
        classUnderTest.saveConfiguration(configuration, context)

        // Assert
        Assert.assertEquals(expectedValue, currentValue)
    }

    companion object {
        fun createWithDependencies(): ConfigurationService {
            return ConfigurationServiceImpl()
        }
    }
}