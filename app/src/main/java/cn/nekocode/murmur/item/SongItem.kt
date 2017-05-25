/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.murmur.item

import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.nekocode.dividerdrawable.DividerDrawable
import cn.nekocode.dividerdrawable.DividerLayout
import cn.nekocode.dividerdrawable.DividerUtils
import cn.nekocode.itempool.Item
import cn.nekocode.murmur.R
import cn.nekocode.murmur.data.DO.douban.Song
import cn.nekocode.murmur.util.Drawables
import kotlinx.android.synthetic.main.item_meizi.view.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class SongItem : Item<SongItem.VO>() {

    override fun onCreateItemView(inflater: LayoutInflater, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.item_meizi, parent, false)

        // 创建 Divider
        val divider = DividerDrawable()
        divider.setStrokeWidth(1)
                .setColor(0xFFEBEBEB.toInt())
                .layout
                .setAlign(DividerLayout.ALIGN_PARENT_BOTTOM)
                .setMarginLeftDp(58)

        // 设置 Item 背景
        view.background = DividerUtils.addDividersTo(
                Drawables.clickableBackground(ContextCompat.getColor(parent.context, R.color.colorPrimaryLight)),
                divider)
        return view!!
    }

    override fun onBindData(vo: SongItem.VO) {
        with (viewHolder.itemView) {
            indexTextView.text = (viewHolder.adapterPosition + 1).toString()
            titleTextView.text = vo.title
            artistTextView.text = vo.artist
        }
    }

    /**
     * ViewObject
     * 负责在 View 层传递数据
     */
    class VO(
            val title: String,
            val artist: String,
            val DO: Any
    ) {
        companion object {
            fun fromSong(song: Song): VO {
                return VO(song.title, song.artist, song)
            }
        }
    }
}