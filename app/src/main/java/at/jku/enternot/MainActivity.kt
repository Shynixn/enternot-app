package at.jku.enternot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.action_settings -> {
            // TODO: Implement show settings activity.
            Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
