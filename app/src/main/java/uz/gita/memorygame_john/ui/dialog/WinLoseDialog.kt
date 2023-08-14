package uz.gita.memorygame_john.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.mymemorygame.databinding.DialogWinLoseBinding

class WinLoseDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogWinLoseBinding
    private var winLoseText: String? = null
    private var menuButtonListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogWinLoseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)

        binding.apply {
            winLoseText.text = this@WinLoseDialog.winLoseText
            btnMenuWinLose.setOnClickListener {
                menuButtonListener?.invoke()
            }
        }
    }

    fun setWinLoseText(winLoseText: String): WinLoseDialog {
        this.winLoseText = winLoseText
        return this
    }

    fun setOnMenuClickListener(action: () -> Unit): WinLoseDialog {
        menuButtonListener = action
        return this
    }
}