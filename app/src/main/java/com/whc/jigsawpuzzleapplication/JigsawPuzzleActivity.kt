package com.whc.jigsawpuzzleapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.whc.jigsawpuzzleapplication.databinding.ActivityJigsawPuzzleBinding
import kotlin.math.abs

@ExperimentalStdlibApi
class JigsawPuzzleActivity : AppCompatActivity() {

    lateinit var imageItemAdapter: ImageItemAdapter
    lateinit var binding: ActivityJigsawPuzzleBinding
    var width = 0
    var height = 0
    var widthSize = 3
    var heightSize = 3
    var total = widthSize*heightSize
    var puzzleModelListData = arrayListOf<PuzzleModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJigsawPuzzleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageItemAdapter = ImageItemAdapter(this)

        width = binding.root.width
        height = binding.root.height
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, widthSize)
            adapter = imageItemAdapter
        }

        val bitmapList = cutImage(readBitmapById(baseContext, R.drawable.bk)!!)
        puzzleModelListData = upsetImage(bitmapList)
        puzzleModelListData.sortBy { it.currentPosition }
        imageItemAdapter.setList(puzzleModelListData)
        var posX = 0f
        var curPosX = 0f
        var posY = 0f
        var curPosY = 0f

        var moveTime = System.currentTimeMillis()
        binding.mainLayout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    posX = event.x
                    posY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    curPosX = event.x
                    curPosY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    if ((System.currentTimeMillis() - moveTime) > 200) {
                        println("currentTime${System.currentTimeMillis()} moveTime${moveTime}")
                        var x = 0
                        if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > 25)) {
                            x = 2
                        } else if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > 25)) {
                            x = 1
                        }
                        var y = 0
                        if ((curPosY - posY > 0) && (Math.abs(curPosY - posY) > 25)) {
                            y = 1
                        } else if ((curPosY - posY < 0) && (Math.abs(curPosY - posY) > 25)) {
                            y = 2
                        }
                        if (x > 0 || y > 0) {
                            if (x > 0 && y > 0) {
                                if (abs(curPosX - posX) > abs(curPosY - posY)) {
                                    if (x == 1) {
                                        move(3)
                                    } else {
                                        move(4)
                                    }
                                } else {
                                    if (y == 2) {
                                        move(1)
                                    } else {
                                        move(2)
                                    }
                                }
                            } else {
                                if (x == 0) {
                                    if (y == 2) {
                                        move(1)
                                    } else {
                                        move(2)
                                    }
                                }
                                if (y == 0) {
                                    if (x == 1) {
                                        move(3)
                                    } else {
                                        move(4)
                                    }
                                }
                            }


                        }
                        moveTime = System.currentTimeMillis()
                    }

                }
            }
            true
        }
    }

    private fun move(type: Int) {
        val white = puzzleModelListData.find { it.isWhite }!!
        var movePosition = 0
        when (type) {
            1 -> {
                Log.v("whc", "向上滑动")
                val between = widthSize*(heightSize-1)+1..total
                if (white.currentPosition in between){
                    return
                }
                movePosition = white.currentPosition+widthSize

            }
            2 -> {
                Log.v("whc", "向下滑动")
                val between = 1..widthSize
                if (white.currentPosition in between){
                    return
                }
                movePosition = white.currentPosition-widthSize

            }
            3 -> {
                Log.v("whc", "向左滑动")
                val list = arrayListOf<Int>()

                for (i in 1..heightSize){
                    list.add(i*widthSize)
                }
                if (white.currentPosition in list){
                    return
                }
                movePosition = white.currentPosition+1
            }
            4 -> {
                Log.v("whc", "向右滑动")
                val list = arrayListOf<Int>()
                for (i in 0 until heightSize){
                    list.add(i*widthSize+1)
                }
                if (white.currentPosition in list){
                    return
                }
                movePosition = white.currentPosition-1
            }
        }
        puzzleModelListData.find { it.currentPosition==movePosition }?.let {
            it.currentPosition = white.currentPosition
            white.currentPosition = movePosition
            puzzleModelListData.sortBy { it.currentPosition }
            imageItemAdapter.setList(puzzleModelListData)
        }
        check()
    }

    private fun check() {
        if (puzzleModelListData.all { it.position == it.currentPosition }) {
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cutImage(bitmap: Bitmap): List<Bitmap> {
        val bitmapList = arrayListOf<Bitmap>()
        val bitmapWidth = bitmap.width / widthSize
        val bitmapHeight = bitmap.height / heightSize
        for (i in 0 until heightSize) {
            for (j in 0 until widthSize) {
                val newBitmap = Bitmap.createBitmap(
                    bitmap,
                    j * bitmapWidth,
                    i * bitmapHeight,
                    bitmapWidth,
                    bitmapHeight
                )
                bitmapList.add(newBitmap)
            }
        }
        return bitmapList
    }

    private fun upsetImage(bitmapList: List<Bitmap>): ArrayList<PuzzleModel> {
        val temps = bitmapList.take(bitmapList.size - 1)
        val puzzleModelList = arrayListOf<PuzzleModel>()
        val list = arrayListOf<Int>()
        for (i in 1 .. total) {
            list.add(i)
        }
        list.shuffle()
        temps.forEachIndexed { index, bitmap ->
            val puzzleModel = PuzzleModel(bitmap, index, list.removeFirst(), false)
            puzzleModelList.add(puzzleModel)
        }

        puzzleModelList.add(
            PuzzleModel(
                readBitmapById(baseContext, R.drawable.wbk)!!,
                total,
                list.removeFirst(), true
            )
        )

        return puzzleModelList
    }


    class ImageItemAdapter(
        private
        val activity: JigsawPuzzleActivity
    ) : BaseQuickAdapter<PuzzleModel, BaseViewHolder>(R.layout.item_image) {
        override fun convert(holder: BaseViewHolder, item: PuzzleModel) {
            val imageView = holder.getView<ImageView>(R.id.imageView)
            val height = activity.binding.root.height / activity.heightSize
            val layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            imageView.layoutParams = layoutParams
            holder.setImageBitmap(R.id.imageView, item.bitMap)
        }
    }

    private fun readBitmapById(context: Context, resId: Int): Bitmap? {
        val opt = BitmapFactory.Options()
        opt.inPreferredConfig = Bitmap.Config.RGB_565
        opt.inPurgeable = true
        opt.inInputShareable = true

        val inputStream = context.resources.openRawResource(resId)
        return BitmapFactory.decodeStream(inputStream, null, opt)
    }
}