package com.kanush_productions.drawingapp



import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.lifecycle.lifecycleScope
import com.kanush_productions.drawingapp.DrawingClass.Companion.currentBrush
import com.kanush_productions.drawingapp.DrawingClass.Companion.pathList
import com.kanush_productions.drawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var drawingClass: DrawingClass
    lateinit var backgroundImage: ImageView

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == RESULT_OK && result.data != null){
                drawingClass.clearAll()

                //bind.ivBackground.scaleType = ImageView.ScaleType.CENTER_CROP
                bind.ivBackground.setImageURI(result.data?.data)
                //drawingClass.background = croppedImage.drawable
            }
        }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
            permissions.entries.forEach{
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted){
                    //Toast.makeText(this@MainActivity, "Permission granted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(intent)
                } else {
                    if(permissionName == Manifest.permission.READ_MEDIA_IMAGES) {
                        Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this@MainActivity, "Permission denied lol", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        bind = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        drawingClass = findViewById(R.id.drawing_layout)
        val backgroundImage: ImageView = findViewById(R.id.iv_background)
        bind.brushSeekbar.progress = 4
        colorPaletteListener()
        resetListener()
        saveListener()
        backGroundFillListener()
        undoListener()
        eraserListener()
        brushSizeListener()
        seekBarListener()
        importImageListener()
    }


    private fun getWidth(): Int {
        return drawingClass.width
    }

    private fun getHeight(): Int {
        return drawingClass.height
    }


    private fun colorPaletteListener(){
        bind.colorPalette.setOnClickListener {
            if (bind.paletteLl.visibility == View.INVISIBLE) bind.paletteLl.visibility = View.VISIBLE
            else bind.paletteLl.visibility = View.INVISIBLE
        }
            bind.colorBlackPicker.setOnClickListener {
                paintBrush.color = getColor(R.color.black)
                currentColor(paintBrush.color)
                bind.paletteLl.visibility = View.INVISIBLE
            }
            bind.colorGreenPicker.setOnClickListener {
                paintBrush.color = getColor(R.color.green)
                currentColor(paintBrush.color)
                bind.paletteLl.visibility = View.INVISIBLE
            }
            bind.colorRedPicker.setOnClickListener {
                paintBrush.color = getColor(R.color.red)
                currentColor(paintBrush.color)
                bind.paletteLl.visibility = View.INVISIBLE
            }
            bind.colorYellowPicker.setOnClickListener {
                paintBrush.color = getColor(R.color.yellow)
                currentColor(paintBrush.color)
                bind.paletteLl.visibility = View.INVISIBLE
            }
    }

    private fun backGroundFillListener() {
        bind.fillBtn.setOnClickListener {
            if (bind.fillLl.visibility == View.GONE) {
                bind.fillLl.visibility = View.VISIBLE
            } else bind.fillLl.visibility = View.GONE

        }
        bind.fillColorBlack.setOnClickListener {
            drawingClass.fillPaint(getColor(R.color.black))
            isBackgroundFilled = true
            drawingClass.invalidate()
            drawingClass.clearCanvas()
        }
        bind.fillColorBlue.setOnClickListener {
            drawingClass.fillPaint(getColor(R.color.blue))
            isBackgroundFilled = true
            drawingClass.invalidate()
            drawingClass.clearCanvas()
            }
        bind.fillColorGreen.setOnClickListener {
            drawingClass.fillPaint(getColor(R.color.green))
            isBackgroundFilled = true
            drawingClass.invalidate()
            drawingClass.clearCanvas()
        }
        bind.fillColorPurple.setOnClickListener {
            drawingClass.fillPaint(getColor(R.color.purple))
            isBackgroundFilled = true
            drawingClass.invalidate()
            drawingClass.clearCanvas()
            }
        bind.fillColorRed.setOnClickListener {
            drawingClass.fillPaint(getColor(R.color.red))
            isBackgroundFilled = true
            drawingClass.invalidate()
            drawingClass.clearCanvas()
            }
        bind.fillColorWhite.setOnClickListener {
            drawingClass.fillPaint(getColor(R.color.white))
            isBackgroundFilled = true
            drawingClass.invalidate()
            drawingClass.clearCanvas()
        }
    }

        private fun undoListener() {
            bind.undo.setOnClickListener {
                drawingClass.undo()

            }
        }
    private fun eraserListener(){
        bind.eraserBtn.setOnClickListener {
            paintBrush.color = getColor(R.color.white)
            currentColor(paintBrush.color)
        }
    }
        private fun resetListener() {
            bind.clearBtn.setOnClickListener {
                path = Path()
                bind.ivBackground.setImageDrawable(null)
                drawingClass.clearAll()
                drawingClass.invalidate()
            }
        }
    private fun wrenchListener(){
        bind.wrenchBtn.setOnClickListener {

        }
    }



    private fun brushSizeListener(){
        bind.brushSize.setOnClickListener {
            if (bind.brushLl.visibility == View.GONE) bind.brushLl.visibility = View.VISIBLE
            else bind.brushLl.visibility = View.GONE
        }
    }
    private fun seekBarListener(){
        bind.brushSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
                strokeWidthMain = progress.toFloat()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }
        private fun importImageListener(){
            bind.importBtn.setOnClickListener {
                requestStoragePermission()
            }
        }
        private fun saveListener() {
            bind.saveBtn.setOnClickListener {
//                lifecycleScope.launch{
//                    saveImageWeird(getBitmapFromView(bind.canvasId))
//                }
            saveImage()
            }
        }
    private fun saveImage(){
        MediaStore.Images.Media.insertImage(
            contentResolver, getBitmapFromView(bind.canvasId), "Drawing", "Drawing")
            Toast.makeText(this, "Saved to gallery!", Toast.LENGTH_SHORT).show()
    }

        suspend fun saveImageWeird(mBitmap:Bitmap?):String {
            // Получить Bitmap с нарисованным
            //val bitmap = drawingClass.getDrawingBitmap(getWidth(), getHeight())
            //val bitmap = drawingClass.mCanvasBitmap
            //bitmap?.eraseColor(Color.WHITE)
            // Сохранить в папку Pictures
            var result = ""
            withContext(Dispatchers.IO) {
                if(mBitmap != null ) {
                    try{
                        val bytes = ByteArrayOutputStream()
                        getBitmapFromView(drawingClass).compress(Bitmap.CompressFormat.PNG, 100, bytes)
                        val f = File(filesDir.absoluteFile.toString()
                                + File.separator + "Drawing" + System.currentTimeMillis() / 1000 + "_PNG")
                        val fo = FileOutputStream(f)
                        fo.write(bytes.toByteArray())
                        fo.close()
                        result = f.absolutePath
                        runOnUiThread {
                            if (result.isNotEmpty()){
                                Toast.makeText(this@MainActivity, "image saved: $result", Toast.LENGTH_SHORT ).show()
                            } else {
                                Toast.makeText(this@MainActivity, "saving failed", Toast.LENGTH_SHORT ).show()
                            }
                        }
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return result




        }
        private fun currentColor(color: Int) {
            //path = Path()
            currentBrush = color
        }

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message).setPositiveButton("Cancel") {dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
           this,
            Manifest.permission.READ_MEDIA_IMAGES)
        ){
            showRationaleDialog("Drawing App", "Необходимо разрешение для доступа к галерее")
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            showRationaleDialog("Drawing App", "Необходимо разрешение для доступа к галерее")
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ))
            } else {
                requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
//        bind.ivBackground.scaleType = ImageView.ScaleType.CENTER_CROP
//        view.background = bind.ivBackground.drawable
//        val bgDrawable = view.background
//        if (bgDrawable != null) bgDrawable.draw(canvas)
//        else canvas.drawColor(Color.WHITE)

        val bgDrawable = bind.ivBackground.drawable
        if (bind.ivBackground.drawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }
    companion object {
        var path = Path()
        var paintBrush = Paint()
        var strokeWidthMain = 4f
        var isBackgroundFilled = false
    }
}

