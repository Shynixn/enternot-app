package at.jku.enternot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import org.koin.android.architecture.ext.viewModel

class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModelImpl by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel.playSiren()
    }
}
