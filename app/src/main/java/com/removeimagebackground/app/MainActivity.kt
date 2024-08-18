package com.removeimagebackground.app

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var ivOriginalImage: ImageView
    private lateinit var ivResultImage: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPickImage: Button = findViewById(R.id.btnPickImage)
        val btnRemoveBackground: Button = findViewById(R.id.btnRemoveBackground)
        ivOriginalImage = findViewById(R.id.ivOriginalImage)
        ivResultImage = findViewById(R.id.ivResultImage)

        btnPickImage.setOnClickListener {
            ImagePicker.with(this)
                .crop() // Crop image
                .compress(1024) // Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                ) // Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        btnRemoveBackground.setOnClickListener {
            imageUri?.let { uri ->
                removeBackground(uri)
            } ?: run {
                Toast.makeText(this, "Please pick an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            ivOriginalImage.setImageURI(imageUri)
        }
    }

    private fun removeBackground(uri: Uri) {
        val file = File(uri.path) // Convert URI to File
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("image_file", file.name, requestFile)

        RetrofitClient.instance.removeBg("tQVSRqjDg6Z4hHuFKGF7pGNa", body)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val resultImage = BitmapFactory.decodeStream(response.body()?.byteStream())
                        ivResultImage.setImageBitmap(resultImage)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to remove background",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}
