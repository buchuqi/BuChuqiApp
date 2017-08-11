package jd.com.library.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * 获取用户唯一标识
 * Created by jian_zhou on 2017/5/16.
 */

public class UniqueIdUtil {
    private Context context;
    private boolean isPermit;
    private static UniqueIdUtil sInstance;

    public UniqueIdUtil() {

    }

    /**
     * Return the single SharedPreferences instance.
     */
    public static UniqueIdUtil getInstance() {
        if (sInstance == null) {
            sInstance = new UniqueIdUtil();
        }
        return sInstance;
    }

    public void setPermit(boolean isPermit){
        this.isPermit = isPermit;
    }

    /**
     * 获取唯一标识
     *
     * @param context
     * @return
     */
    public String getuniqueId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        String uniqueId = null;
        if (isPermit) {
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            uniqueId = deviceUuid.toString();
        } else {
            uniqueId = "00000";
        }
        return uniqueId;
    }


}
