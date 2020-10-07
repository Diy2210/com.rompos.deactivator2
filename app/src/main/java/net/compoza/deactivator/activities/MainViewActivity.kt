package net.compoza.deactivator.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jetbrains.handson.mpp.mobile.R
import net.compoza.deactivator.fragments.ListFragment

class MainViewActivity : AppCompatActivity() {

    private var listFragment = ListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_view, listFragment)
                .commitAllowingStateLoss()
        }
    }
}