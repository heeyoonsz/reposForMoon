package ds.project.tadaktadakfront

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_img.*

class ImgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img)


        val currentPhotoPath: String
        currentPhotoPath= getIntent().getStringExtra("path")!!;
        tv_image_path.setText(currentPhotoPath);
        Glide.with(this).load(currentPhotoPath).into(iv_image);
    }
}

