package com.drspaceman.atomicio.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drspaceman.atomicio.R
import kotlinx.android.synthetic.main.activity_identity_detail.*

class IdentityDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity_detail)

        setSupportActionBar(toolbar)
    }
}
