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

package cn.nekocode.murmur.data.DO.leancloud

import android.os.Parcel
import android.os.Parcelable
import cn.nekocode.murmur.data.DO.WithId
import com.google.gson.annotations.SerializedName

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
data class LeanCloudFile(
        @SerializedName("objectId") override val id: String,
        @SerializedName("mime_type") val mimeType: String,
        var url: String
) : WithId, Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<LeanCloudFile> = object : Parcelable.Creator<LeanCloudFile> {
            override fun createFromParcel(source: Parcel): LeanCloudFile = LeanCloudFile(source)
            override fun newArray(size: Int): Array<LeanCloudFile?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(mimeType)
        dest.writeString(url)
    }
}