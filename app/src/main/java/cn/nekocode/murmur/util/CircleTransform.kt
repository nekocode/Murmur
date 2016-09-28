package cn.nekocode.murmur.util

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

/**
 * @author nekocode (nekocode.cn@gmail.com)
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