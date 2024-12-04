package com.example.tippy

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.tippy.ui.theme.TippyTheme

private const val TAG = "MainActivity"

private const val INITIAL_TIP_PERCENT = 15
private const val SEEKBAR_MAX = 30

class MainActivity : ComponentActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount : TextView
    private lateinit var tvTipDescription : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvPercent = findViewById(R.id.tvPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        seekBarTip.max = SEEKBAR_MAX

        tvPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //Log.i(TAG, "onProgressChanged $progress")
                tvPercent.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                //Log.i(TAG,"afterTextChange $s")
                computeTipAndTotal()

            }
        })
    }

    private fun updateTipDescription(TipPercent: Int) {
        val normalPercent = ((TipPercent.toDouble() / SEEKBAR_MAX.toDouble()) * 100).toInt()
        //Log.i(TAG, "normalized Percent: $normalPercent")
        //Log.i(TAG, "Tip Percent: $TipPercent")
        //Log.i(TAG, "Bar Percent: $SEEKBAR_MAX")
        val tipDescription = when (normalPercent){
            in 0..20 -> "Poor"
            in 21..40 -> "Acceptable"
            in 41..60 -> "Good"
            in 61..80 -> "Great"
            else -> "Excellent"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            (normalPercent.toFloat()/100),
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        // get value of base and percent
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount*tipPercent/100
        val totalAmount = baseAmount+tipAmount
        // update UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}
