package cn.nekocode.murmur.data.repo

import com.orhanobut.hawk.Hawk

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object SettingRepo {

    fun loadSelectedMurmursIDs(): List<String>? {
        return Hawk.get("selectedMurmursIDs")
    }

    fun saveSelectedMurmursIDs(murmursIDs: List<String>?) {
        Hawk.put("selectedMurmursIDs", murmursIDs)
    }

}