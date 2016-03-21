package cn.nekocode.murmur.data.model

import cn.nekocode.murmur.data.dto.Murmur
import com.orhanobut.hawk.Hawk

/**
 * Created by nekocode on 16/3/22.
 */
object SettingModel {

    fun loadSelectedMurmurs(): List<Murmur>? {
        return Hawk.get("selectedMurmurs")
    }

    fun saveSelectedMurmurs(murmurs: List<Murmur>?) {
        Hawk.put("selectedMurmurs", murmurs)
    }

}