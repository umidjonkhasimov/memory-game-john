package uz.gita.memorygame_john.repository

import com.example.mymemorygame.R
import uz.gita.memorygame_john.data.CardData

class AppRepository private constructor() {
    private val cardList = ArrayList<CardData>()

    companion object {
        private var instance: AppRepository? = null

        fun getInstance(): AppRepository {
            if (instance == null)
                instance = AppRepository()
            return instance!!
        }
    }

    init {
        cardList.add(CardData(R.drawable.image_1, 1))
        cardList.add(CardData(R.drawable.image_2, 2))
        cardList.add(CardData(R.drawable.image_3, 3))
        cardList.add(CardData(R.drawable.image_4, 4))
        cardList.add(CardData(R.drawable.image_5, 5))
        cardList.add(CardData(R.drawable.image_6, 6))
        cardList.add(CardData(R.drawable.image_7, 7))
        cardList.add(CardData(R.drawable.image_8, 8))
        cardList.add(CardData(R.drawable.image_9, 9))
        cardList.add(CardData(R.drawable.image_10, 10))
        cardList.add(CardData(R.drawable.image_11, 11))
        cardList.add(CardData(R.drawable.image_12, 12))
        cardList.add(CardData(R.drawable.image_13, 13))
        cardList.add(CardData(R.drawable.image_14, 14))
        cardList.add(CardData(R.drawable.image_15, 15))
        cardList.add(CardData(R.drawable.image_16, 16))
        cardList.add(CardData(R.drawable.image_17, 17))
        cardList.add(CardData(R.drawable.image_18, 18))
        cardList.add(CardData(R.drawable.image_19, 19))
        cardList.add(CardData(R.drawable.image_20, 20))
    }

    fun getData(count: Int): List<CardData> {
        cardList.shuffle()
        val ls = cardList.subList(0, count / 2)
        val result = ArrayList<CardData>(count)
        result.addAll(ls)
        result.addAll(ls)
        result.shuffle()
        return result
    }
}