package cn.nekocode.murmur.util

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * Created by nekocode on 3/15/16.
 */
class CircleTransform: Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val target = ImageUtil.getCircleBitmap(source)
        source.recycle()
        return target
    }

    override fun key(): String {
        return "circle"
    }

}