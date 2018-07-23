package work.nityc_nyuta.mockline.Activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import work.nityc_nyuta.mockline.R

class QRReadActivity : AppCompatActivity() {
    private var qrReader: DecoratedBarcodeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_read)

        title = "QRコードリーダー"

        // QR読み取り開始
        qrReader = findViewById<DecoratedBarcodeView>(R.id.qr_reader)
        qrReader!!.setStatusText("QRを画面に合わせてください")

        // 読み取り結果がnullでなければIntentを返す
        qrReader!!.decodeSingle(object: BarcodeCallback{
            override fun barcodeResult(result: BarcodeResult?) {
                if(result != null){
                    val retIntent = Intent()
                    retIntent.putExtra("Result", result.text)
                    setResult(Activity.RESULT_OK, retIntent)

                    qrReader!!.pause()
                    finish()
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })

        // カメラ表示
        qrReader!!.resume()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(qrReader != null) {
            qrReader!!.pause()
        }
    }
}