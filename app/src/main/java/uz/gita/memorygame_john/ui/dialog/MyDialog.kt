package uz.gita.memorygame_john.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import com.example.mymemorygame.R
import com.example.mymemorygame.databinding.DialogBinding

class MyDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogBinding
    private var positiveButtonListener: (() -> Unit)? = null
    private var negativeButtonListener: (() -> Unit)? = null
    private var levelText: String? = null
    private var scoreText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)

        binding.btnNext.setOnClickListener {
            positiveButtonListener?.invoke()
        }

        binding.btnMenu.setOnClickListener {
            negativeButtonListener?.invoke()
        }

        levelText?.let {
            binding.level.text = it
        }

        scoreText?.let {
            binding.score.text = scoreText
        }
    }

    fun setNextButton(action: () -> Unit): MyDialog {
        positiveButtonListener = action
        return this
    }

    fun setMenuButton(action: () -> Unit): MyDialog {
        negativeButtonListener = action
        return this
    }

    fun setLevel(level: String): MyDialog {
        levelText = level
        return this
    }

    fun setScore(score: String): MyDialog {
        scoreText = score
        return this
    }
}