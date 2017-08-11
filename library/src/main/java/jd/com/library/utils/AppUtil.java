package jd.com.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppUtil {
    // / 没有连接
    public static final int NETWORN_NONE = 0;
    // / wifi连接
    public static final int NETWORN_WIFI = 1;
    // / 3G连接
    public static final int NETWORN_MOBILE = 2;

    /**
     * 设备的名称
     */
    public static final String FileName = "equidname";
    /**
     * 当前选中的屏体信息类
     */
    public static final String EquipEntity = "entity";

    /* byte[]转Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 25) & 0xFF000000);
        return addr;

    }

    /* Int转byte[] */
    public static byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    /**
     * 获取手机的SerialNumber
     *
     * @return result is same to getSerialNumber1()
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 判断当前否联网
     *
     * @param context 上下文
     * @return true联网, 否则没联网
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (!wifi) {
        }
        return true;
    }

    /**
     * 是否是wifi情况下
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (!wifi) {
            return wifi;
        }
        return true;
    }

    /**
     * 返回当前网络连接类型
     *
     * @param context 上下文
     * @return
     */
    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // Wifi
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (state == State.CONNECTED || state == State.CONNECTING) {
            return NETWORN_WIFI;
        }
        // 3G
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        if (state == State.CONNECTED || state == State.CONNECTING) {
            return NETWORN_MOBILE;
        }
        return NETWORN_NONE;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getSystomDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);

        return str;

    }

    /**
     * 获取百分比
     *
     * @param x
     * @param total
     * @return
     */
    public static String getPercent(int x, int total) {

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 保留2位小数
        numberFormat.setMaximumFractionDigits(0);
        String result = numberFormat.format((float) x / (float) total * 100);

        if (result.equals("NaN")) {
            return "0%";
        } else {
            return result + "%";
        }

    }

    /**
     * 获取百分比
     *
     * @param x
     * @param total
     * @return
     */
    public static float getPercentNum(int x, int total) {

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 保留2位小数
        numberFormat.setMaximumFractionDigits(0);

        return (float) x / (float) total;
    }


    public static String LeftPad_Tow_Zero(int str) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("00");
        return format.format(str);

    }

    /**
     * TODO(获取缩略图)
     *
     * @param $fileName
     * @param $width
     * @param $height
     * @return Bitmap
     * @author
     */
    public static Bitmap getThumbnail(String $fileName, int $width, int $height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile($fileName, options);
        options.inJustDecodeBounds = false;
        // 缩放比
        int scale;
        int widthScale = (int) (options.outWidth / (float) $width);
        int heightScale = (int) (options.outHeight / (float) $height);
        scale = widthScale > heightScale ? widthScale : heightScale;
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile($fileName, options);
        // Bitmap bit = compressImage(bitmap);
        // savePhotoToSDCard(bit, picPath, picName);
        return bitmap;
    }


    /**
     * 获取app的版本
     *
     * @param context
     * @return
     */
    public static int getAppPackNum(Context context) {
        int packageNum;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            packageNum = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            packageNum = -1;
        }
        return packageNum;
    }

    public static String getAppCodeName(Context context) {
        String CodeName;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            CodeName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            CodeName = "";
        }
        return CodeName;
    }

    /**
     * 获取设备唯一编号
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;
    }

    /**
     * @param context
     * @return
     */
    public static String getAndrodID(Context context) {

        return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

    }

    /**
     * 比较日期
     *
     * @param format yyyy-MM-dd HH:mm:ss
     * @param s1
     * @param s2
     * @return
     * @throws Exception
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean DateCompare(String format, String s1, String s2)
            throws Exception {
        // 设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 得到指定模范的时间
        Date d1 = sdf.parse(s1);
        Date d2 = sdf.parse(s2);
        // 比较
        if (d1.getTime() > d2.getTime()) {
            return false;
        } else {
            return true;
        }
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }


    /**
     * @param checkData
     * @return
     * @author
     */
    public static boolean isObjectDataNull(Object checkData) {
        if ("".equals(checkData) || checkData == null) {
            return true;
        }
        return false;
    }

    public static boolean isTwoStringEqual(String aString, String bString) {
        if (aString.equals(bString) || bString == aString) {
            return true;
        }
        return false;
    }

    /**
     * 获取资源文件数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return
     */
    public static boolean isMobilePhone(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            String oneNum = str.substring(0, 1);
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(str);
            if (isNum.matches() && "1".equals(oneNum))
                return true;
            else
                return false;
        }
    }


    /**
     * 邮箱验证
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * //获取完整的域名
     *
     * @param text 获取浏览器分享出来的text文本
     */
    public static String getCompleteUrl(String text) {
        Pattern p = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        matcher.find();
        return matcher.group();
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "没有发现版本号";
        }
    }

    /**
     * 获取屏幕宽
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        return displayMetrics.widthPixels;
    }


    public static int getSW(Activity context) {
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getSH(Activity context) {
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * 获取屏幕高
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 获取IP
     *
     * @return
     */
    public static String getLocalIP() {
        String baseString = "100.100.100.100";
        String mLocalIp = getLocalIpAddress();
        if (isEmpty(mLocalIp)) {
            return baseString;
        }
        try {
            String localVersionloacl[] = mLocalIp.split("\\.");
            if (isObjectDataNull(localVersionloacl)) {
                return baseString;
            }
            if (localVersionloacl.length != 4) {
                return baseString;
            }
            if (isTwoStringEqual("0", localVersionloacl[0])
                    && isTwoStringEqual("0", localVersionloacl[1])
                    && isTwoStringEqual("0", localVersionloacl[2])
                    && isTwoStringEqual("0", localVersionloacl[3])) {
                return baseString;
            }
        } catch (Exception e) {
            return baseString;
        }

        return mLocalIp;
    }


    /**
     * 获取当前的ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "100.100.100.100";
        }
        return null;
    }


    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取唯一标识
     *
     * @param context
     * @return
     */
    public static String getuniqueId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return TextUtils.isEmpty(uniqueId) ? "000" : uniqueId;
    }
}
