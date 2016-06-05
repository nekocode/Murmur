package cn.nekocode.murmur.data.repo

import com.orhanobut.hawk.Hawk

/**
 * Created by nekocode on 16/3/22.
 */
object SettingRepo {

    fun loadSelectedMurmursIDs(): List<String>? {
        return Hawk.get("selectedMurmursIDs")
    }

    fun saveSelectedMurmursIDs(murmursIDs: List<String>?) {
        Hawk.put("selectedMurmursIDs", murmursIDs)
    }

}