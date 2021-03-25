package com.whc.jigsawpuzzleapplication

import android.graphics.Bitmap

/**
 * @author whc
 */
data class PuzzleModel(val bitMap:Bitmap, val position:Int, var currentPosition:Int, var isWhite:Boolean)