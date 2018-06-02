package at.jku.enternot

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_configuration.*

class ConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        toolbar.menu.clear()

        toolbar.inflateMenu(R.menu.configuration_menu)
    }
}
