package uz.gita.memorygame_john.ui.screens

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.dialogFragmentViewBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mymemorygame.R
import uz.gita.memorygame_john.data.CardData
import com.example.mymemorygame.databinding.ScreenGameBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.gita.memorygame_john.data.LevelEnum
import uz.gita.memorygame_john.repository.AppRepository
import uz.gita.memorygame_john.ui.dialog.MyDialog
import uz.gita.memorygame_john.ui.dialog.WinLoseDialog
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class GameScreen : Fragment(R.layout.screen_game) {
    private val binding: ScreenGameBinding by viewBinding(ScreenGameBinding::bind)
    private var defLevel = LevelEnum.EASY
    private val args by navArgs<GameScreenArgs>()
    private val repository = AppRepository.getInstance()
    private var _height = 0
    private var _width = 0
    private val images = ArrayList<ImageView>()
    private val openCards = ArrayList<ImageView>(2)
    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var scoreInt: Int = 0
    private var levelInt: Int = 1
    private var isAnimating: Boolean = false
    private var isStopped: Boolean = false
    private var lastTime: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        defLevel = args.level

        binding.apply {
            level.text = "Level $levelInt"
            score.text = "Score $scoreInt"
            menu.setOnClickListener {
                findNavController().popBackStack()
            }
            reload.setOnClickListener {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_restart)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.findViewById<ImageView>(R.id.btn_menu_restart).setOnClickListener {
                    findNavController().popBackStack()
                    dialog.dismiss()
                }
                dialog.findViewById<ImageView>(R.id.btn_restart_restart).setOnClickListener {
                    countDownTimer?.cancel()
                    restartGame()
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        binding.container.post {
            _height = binding.container.height / defLevel.verCount
            _width = binding.container.width / defLevel.horCount

            val count = defLevel.horCount * defLevel.verCount
            val ls = repository.getData(count)
            describeCardData(ls)
            when (defLevel) {
                LevelEnum.EASY -> {
                    setCountDown(30)
                    openCloseAllCards(1000)
                }

                LevelEnum.MEDIUM -> {
                    setCountDown(40)
                    openCloseAllCards(1500)
                }

                LevelEnum.HARD -> {
                    setCountDown(50)
                    openCloseAllCards(2000)
                }
            }
        }
    }

    private fun restartGame() {
        levelInt = 1
        scoreInt = 0
        binding.apply {
            level.text = "Level $levelInt"
            score.text = "Score $scoreInt"
            container.post {
                _height = binding.container.height / defLevel.verCount
                _width = binding.container.width / defLevel.horCount

                val count = defLevel.horCount * defLevel.verCount
                val ls = repository.getData(count)
                describeCardData(ls)
                when (defLevel) {
                    LevelEnum.EASY -> {
                        setCountDown(30)
                        openCloseAllCards(1000)
                    }

                    LevelEnum.MEDIUM -> {
                        setCountDown(40)
                        openCloseAllCards(1500)
                    }

                    LevelEnum.HARD -> {
                        setCountDown(50)
                        openCloseAllCards(2000)
                    }
                }
            }
        }
    }

    private fun describeCardData(ls: List<CardData>) {
        for (i in 0 until defLevel.horCount) {
            for (j in 0 until defLevel.verCount) {
                binding.apply {
                    val image = ImageView(requireContext())
                    container.addView(image)
                    image.layoutParams = ConstraintLayout.LayoutParams(_width, _height)
                    image.setPadding(12, 12, 12, 12)
                    image.tag = ls[i * defLevel.verCount + j]
                    image.setImageResource(R.drawable.image_back)
                    image.scaleType = ImageView.ScaleType.CENTER_INSIDE

                    image.x = (i * _width * 1f)
                    image.y = (j * _height * 1f)
                    image.scaleX = 0f
                    image.scaleY = 0f
                    image
                        .animate()
                        .scaleY(1f)
                        .scaleX(1f)
                        .setDuration(150)
                        .start()
                    images.add(image)
                }
            }
        }
        setClickListener()
    }

    private fun setClickListener() {
        images.forEach { imageView ->
            imageView.setOnClickListener {
                if (isAnimating) return@setOnClickListener
                openCards.add(imageView)
                imageView.isClickable = false

                imageView.animate()
                    .setDuration(150)
                    .rotationY(89f)
                    .setListener(object : AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {
                            isAnimating = true
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            isAnimating = true
                        }

                        override fun onAnimationCancel(p0: Animator) {

                        }

                        override fun onAnimationRepeat(p0: Animator) {

                        }
                    })
                    .withEndAction {
                        val data = it.tag as CardData
                        imageView.setImageResource(data.imgRes)
                        imageView.rotationY = -89f
                        imageView.animate().setListener(object : AnimatorListener {
                            override fun onAnimationStart(p0: Animator) {
                                isAnimating = true
                            }

                            override fun onAnimationEnd(p0: Animator) {
                                isAnimating = false
                            }

                            override fun onAnimationCancel(p0: Animator) {
                                isAnimating = false
                            }

                            override fun onAnimationRepeat(p0: Animator) {

                            }
                        })
                            .setDuration(150)
                            .rotationY(0f)
                            .withEndAction {
                                if (openCards.size == 2) {
                                    if (areSimilar()) {
                                        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.correct_sound)
                                        mediaPlayer!!.start()

                                        scoreInt += 2
                                        binding.score.text = "Score $scoreInt"
                                        hideCards()
                                        if (hasWon()) {
                                            countDownTimer?.cancel()
                                            if (levelInt <= 9) {
                                                val dialog = MyDialog(requireContext())
                                                dialog.setNextButton {
                                                    levelInt++
                                                    binding.level.text = "Level $levelInt"
                                                    goToNextLevel()
                                                    dialog.dismiss()
                                                }.setMenuButton {
                                                    findNavController().popBackStack()
                                                    dialog.dismiss()
                                                }.setLevel(levelInt.toString())
                                                    .setScore(scoreInt.toString())
                                                    .show()
                                            } else {
                                                val dialog = WinLoseDialog(requireContext())
                                                dialog
                                                    .setWinLoseText("You win\n\nscore: $scoreInt")
                                                    .setOnMenuClickListener {
                                                        findNavController().popBackStack()
                                                        dialog.dismiss()
                                                    }.show()
                                            }
                                        }
                                    } else {
                                        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.wrong_sound)
                                        mediaPlayer!!.start()
                                        closeCards()
                                    }
                                }
                            }.start()
                    }.start()
            }
        }
    }

    private fun goToNextLevel() {
        binding.container.post {
            _height = binding.container.height / defLevel.verCount
            _width = binding.container.width / defLevel.horCount

            val count = defLevel.horCount * defLevel.verCount
            val ls = repository.getData(count)
            describeCardData(ls)
            when (defLevel) {
                LevelEnum.EASY -> {
                    setCountDown(30)
                    openCloseAllCards(1000)
                }

                LevelEnum.MEDIUM -> {
                    setCountDown(40)
                    openCloseAllCards(1500)
                }

                LevelEnum.HARD -> {
                    setCountDown(50)
                    openCloseAllCards(2000)
                }
            }
        }
    }

    private fun openCloseAllCards(millis: Long) {
        lifecycleScope.launch {
            delay(500)
            images.forEach { imageView ->
                imageView
                    .animate().setListener(object : AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {
                            isAnimating = true
                        }

                        override fun onAnimationEnd(p0: Animator) {

                        }

                        override fun onAnimationCancel(p0: Animator) {

                        }

                        override fun onAnimationRepeat(p0: Animator) {

                        }
                    })
                    .setDuration(150)
                    .rotationY(89f)
                    .withEndAction {
                        val data = imageView.tag as CardData
                        imageView.setImageResource(data.imgRes)
                        imageView.rotationY = -89f
                        imageView
                            .animate()
                            .rotationY(0f)
                            .withEndAction {
                                imageView.postDelayed({
                                    imageView
                                        .animate()
                                        .rotationY(89f)
                                        .withEndAction {
                                            imageView.setImageResource(R.drawable.image_back)
                                            imageView.rotationY = -89f
                                            imageView
                                                .animate()
                                                .setListener(object : AnimatorListener {
                                                    override fun onAnimationStart(p0: Animator) {
                                                        isAnimating = true
                                                    }

                                                    override fun onAnimationEnd(p0: Animator) {
                                                        isAnimating = false
                                                    }

                                                    override fun onAnimationCancel(p0: Animator) {
                                                    }

                                                    override fun onAnimationRepeat(p0: Animator) {
                                                    }
                                                })
                                                .rotationY(0f)
                                                .start()
                                        }.start()
                                    countDownTimer?.start()
                                }, millis)
                            }.start()
                    }.start()
            }
        }
    }

    private fun setCountDown(countFrom: Int) {
        binding.progress.apply {
            when (defLevel) {
                LevelEnum.EASY -> {
                    progress = countFrom
                    max = 30
                }

                LevelEnum.MEDIUM -> {
                    progress = countFrom
                    max = 40
                }

                LevelEnum.HARD -> {
                    progress = countFrom
                    max = 50
                }
            }

        }
        binding.timer.text = countFrom.toString()
        countDownTimer = object : CountDownTimer(countFrom * 1000L, 1000L) {
            override fun onTick(millis: Long) {
                val progress = (millis / 1000).toInt()
                binding.progress.progress = progress
                binding.timer.text = progress.toString()
            }

            override fun onFinish() {
                countDownTimer?.cancel()
                val dialog = WinLoseDialog(requireContext())
                dialog
                    .setOnMenuClickListener {
                        findNavController().popBackStack()
                        dialog.dismiss()
                    }
                    .setWinLoseText("You lose\n\nscore: $scoreInt")
                    .show()
            }
        }
    }

    private fun areSimilar(): Boolean {
        val firstImage = openCards[0].tag as CardData
        val secondImage = openCards[1].tag as CardData
        return firstImage.imgRes == secondImage.imgRes && firstImage.id == secondImage.id
    }

    private fun hideCards() {
        isAnimating = true
        images.forEach { imageView ->
            val tag = imageView.tag as CardData
            if (imageView.tag == openCards[0].tag || imageView.tag == openCards[1].tag) {
                imageView.tag = tag.copy(id = tag.id, imgRes = tag.imgRes, isHidden = true)
            }
        }

        lifecycleScope.launch {
            delay(200)
            openCards.forEach { imageView ->
                imageView.animate()
                    .setDuration(150)
                    .scaleX(0f)
                    .scaleY(0f)
                    .start()
            }
            isAnimating = false
            openCards.clear()
        }
    }

    private fun closeCards() {
        isAnimating = true
        lifecycleScope.launch {
            delay(200)
            openCards.forEach { imageView ->
                imageView.isClickable = true
                imageView.animate()
                    .setDuration(200)
                    .rotationY(89f)
                    .withEndAction {
                        imageView.setImageResource(R.drawable.image_back)
                        imageView.rotationY = -89f
                        imageView.animate()
                            .setDuration(150)
                            .rotationY(0f)
                            .start()
                    }.start()
            }
            isAnimating = false
            openCards.clear()
        }
    }

    private fun hasWon(): Boolean {
        images.forEach { imageView ->
            val tag = imageView.tag as CardData
            if (!tag.isHidden)
                return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (isStopped) {
            isStopped = false
            setCountDown(lastTime)
            countDownTimer?.start()
        }
    }

    override fun onStop() {
        super.onStop()
        isStopped = true
        lastTime = binding.timer.text.toString().toInt()
        mediaPlayer?.release()
        countDownTimer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer = null
        countDownTimer = null
    }
}
