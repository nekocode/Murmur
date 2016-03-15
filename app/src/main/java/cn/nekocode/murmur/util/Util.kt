package cn.nekocode.murmur.util

import java.util.*

/**
 * Created by nekocode on 3/13/16.
 */
object Util {

    fun <T> List<T>.randomPick(amount: Int): List<T> {
        val random = java.util.Random(System.currentTimeMillis())
        val size = this.size

        val arrayInt = IntArray(size)
        for(i in 0..(size-1)) {
            arrayInt[i] = i
        }

        var tmp: Int
        var randomInt: Int
        for(i in 0..(size-1)) {
            randomInt = random.nextInt(size)
            tmp = arrayInt[i]
            arrayInt[i] = arrayInt[randomInt]
            arrayInt[randomInt] = tmp
        }

        var limit = amount
        if(limit > size) {
            limit = size
        }

        val listRlt = ArrayList<T>()
        for(i in 0..(limit-1)) {
            listRlt.add(this[arrayInt[i]])
        }

        return listRlt
    }

}