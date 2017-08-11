package jd.com.library.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**展示数据时用到的工具类
 * Created by LRP1989 on 2016/8/27.
 */
public class DataDisplayUtils {

    /**
     * 获取秒数转成10：10格式的显示
     * @param seconds 秒数 210
     * @return  03：30
     */
    public static String getFormatTime(int seconds) {
        String time = "" ;
        int minute = seconds / 60 ;
        int second = seconds % 60 ;

        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;

        if( second < 10 ){
            time += "0" ;
        }
        time += second ;

        return time ;
    }

    /**获取将时间戳秒数格式化后的日期 精确到秒
     * 例：输入所要转换的时间戳输入例如（1402733340）输出（"2016年09月14日16时09分00秒"）
     *
     * @param time  服务器返回的秒数1402733340
     * @return
     */
    public static String getFormatDateDetail(int time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @SuppressWarnings("unused")
        String times = sdr.format(new Date(time * 1000L));

        return times;
    }
    /**获取将时间戳秒数格式化后的日期
     * 例：输入所要转换的时间戳输入例如（1402733340）输出（"2016年09月14日"）
     *
     * @param time  服务器返回的秒数1402733340
     * @return
     */
    public static String getFormatDate(int time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");

        @SuppressWarnings("unused")
        String times = sdr.format(new Date(time * 1000L));

        return times;
    }

    /**
     * 生日格式化
     *
     * @param birthday 生日
     * @return yyyy-MM-dd
     */
    public static String birthdayDateFormat(long birthday) {
        Date date = new Date(birthday);
        String sd = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return sd;
    }

    /**获取题目选项的英文编号的数组
     * @return
     */
    public static char[] getEnNumChars(){
        String s = "ABCDEFGHIJK";
        char[] chars = s.toCharArray();
        return chars;
    }



}