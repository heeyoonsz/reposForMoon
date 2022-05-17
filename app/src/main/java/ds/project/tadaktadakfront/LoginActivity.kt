package ds.project.tadaktadakfront

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button



class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnlogin = findViewById<Button>(R.id.btn_login)
        btnlogin.setOnClickListener{
            val myIntenet = Intent(this, MainActivity::class.java)
            startActivity(myIntenet)
        }

    }
}