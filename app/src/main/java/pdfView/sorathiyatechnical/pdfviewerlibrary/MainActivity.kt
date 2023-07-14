package pdfView.sorathiyatechnical.pdfviewerlibrary

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.artifex.mupdfdemo.MuPDFCore
import com.artifex.mupdfdemo.MuPDFPageAdapter
import com.artifex.mupdfdemo.MuPDFReaderView
import com.artifex.mupdfdemo.MuPDFView
import com.artifex.mupdfdemo.ReaderView

class MainActivity : AppCompatActivity() {
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101
    private val filePath = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Documents/તેજાણી કેયુર સજોડે  આમંત્રણ.pdf" // 文件路径
    private var muPDFCore: MuPDFCore? = null
    private var muPDFReaderView: MuPDFReaderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        initView()
    }
    private fun initView() {
        muPDFReaderView = findViewById<View>(R.id.mupdfReader) as MuPDFReaderView
        // 通过MuPDFCore打开pdf文件
        muPDFCore = openFile(filePath)
        // 判断如果core为空，提示不能打开文件
        if (muPDFCore == null) {
            val alert = AlertDialog.Builder(this).create()
            alert.setTitle(R.string.cannot_open_document)
            alert.setButton(
                AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss)
            ) { dialog, which -> finish() }
            alert.setOnCancelListener { finish() }
            alert.show()
            return
        }
        // 显示
        muPDFReaderView!!.adapter = MuPDFPageAdapter(this, muPDFCore)
    }

    /**
     * 打开文件
     * @param path 文件路径
     * @return
     */
    private fun openFile(path: String): MuPDFCore? {
        Log.e(
            ContentValues.TAG,
            "Trying to open $path"
        )
        try {
            muPDFCore = MuPDFCore(this, path)
        } catch (e: Exception) {
            Log.e(
                ContentValues.TAG,
                "openFile catch:$e"
            )
            return null
        } catch (e: OutOfMemoryError) {
            //  out of memory is not an Exception, so we catch it separately.
            Log.e(
                ContentValues.TAG,
                "openFile catch: OutOfMemoryError $e"
            )
            return null
        }
        return muPDFCore
    }

    override fun onStart() {
        muPDFCore?.startAlerts()
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        if (muPDFCore != null) {
            muPDFCore!!.stopAlerts()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (muPDFReaderView != null) {
            muPDFReaderView!!.applyToChildren(object : ReaderView.ViewMapper() {
                override fun applyToView(view: View) {
                    (view as MuPDFView).releaseBitmaps()
                }
            })
        }
        if (muPDFCore != null) muPDFCore!!.onDestroy()
        muPDFCore = null
        super.onDestroy()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                val perms: MutableMap<String, Int> = HashMap()
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                var i = 0
                while (i < permissions.size) {
                    perms[permissions[i]] = grantResults[i]
                    i++
                }
                val storage =
                    perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                if (storage) {
                    Toast.makeText(this, "Allow storage permissions", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Collection permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * 申请读写权限
     */
    private fun checkPermissions() {
        val permissions: MutableList<String> = ArrayList()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!permissions.isEmpty()) {
            val params = permissions.toTypedArray()
            ActivityCompat.requestPermissions(this, params, WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
        }
    }
}