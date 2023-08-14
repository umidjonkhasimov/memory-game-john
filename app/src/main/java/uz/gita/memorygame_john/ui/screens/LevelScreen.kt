package uz.gita.memorygame_john.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mymemorygame.R
import uz.gita.memorygame_john.data.LevelEnum
import com.example.mymemorygame.databinding.ScreenLevelBinding

class LevelScreen : Fragment(R.layout.screen_level) {
    private val binding: ScreenLevelBinding by viewBinding(ScreenLevelBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            easy.setOnClickListener {
                openGameScreen(LevelEnum.EASY)
            }
            medium.setOnClickListener {
                openGameScreen(LevelEnum.MEDIUM)
            }
            hard.setOnClickListener {
                openGameScreen(LevelEnum.HARD)
            }
        }
    }

    private fun openGameScreen(level: LevelEnum) {
        findNavController().navigate(LevelScreenDirections.actionLevelScreenToGameScreen(level))
    }
}