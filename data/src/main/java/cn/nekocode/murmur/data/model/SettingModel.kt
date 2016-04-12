package cn.nekocode.murmur.data.model

import cn.nekocode.murmur.data.dto.Murmur
import com.orhanobut.hawk.Hawk

/**
 * Created by nekocode on 16/3/22.
 */
object SettingModel {

    fun loadSelectedMurmursIDs(): List<String>? {
        return Hawk.get("selectedMurmursIDs")
    }

    fun saveSelectedMurmursIDs(murmursIDs: List<String>?) {
        Hawk.put("selectedMurmursIDs", murmursIDs)
    }

}