/**
 * Copyright © 2014 All rights reserved.
 *
 * @Title: DataStoreUtil.java
 * @Prject: AndroidBase
 * @Package: com.android.base.utils
 * @Description: TODO
 * @author: raot  719055805@qq.com
 * @date: 2014年4月12日 下午8:29:04
 * @version: V1.0
 */
package jd.com.library.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 数据存储工具类
 */
public class DataStoreUtil {
    private Context context;
    private SharedPreferences mSharePrefs = null;
    private SharedPreferences.Editor editor;
    public static final String Pref_Name = "default_sharedpreferences";
    private static DataStoreUtil sInstance;

    /**
     * Return the single SharedPreferences instance.
     */
    public static DataStoreUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataStoreUtil(context);
            sInstance.open();
        }
        return sInstance;
    }


    public DataStoreUtil(Context context) {
        this.context =  context.getApplicationContext();
    }

    /**
     * Get a sharePreference instance.
     */
    public void open() {
        mSharePrefs = context.getSharedPreferences(Pref_Name, 0);
    }

    public void close() {
        sInstance = null;
        mSharePrefs = null;
    }

    public void saveKey(String mKey, String mValue) {
        if (mSharePrefs != null) {
            editor = mSharePrefs.edit();
            editor.putString(mKey, mValue);
            editor.commit();
        }
    }

    public String getKeyStringValue(String mKey, String mDefValue) {
        String mStr = null;
        if (mSharePrefs != null) {
            mStr = mSharePrefs.getString(mKey, mDefValue);
        } else {
            mStr = mDefValue;
        }
        return mStr;
    }

    public int getKeyIntValue(String mKey, int mDefValue) {
        int mInt = 0;
        if (mSharePrefs != null) {
            mInt = mSharePrefs.getInt(mKey, mDefValue);
        }
        return mInt;
    }


    public boolean getKeyBooleanValue(String mKey, boolean mDefValue) {
        boolean mBool = false;
        if (mSharePrefs != null) {
            mBool = mSharePrefs.getBoolean(mKey, mDefValue);
        }
        return mBool;
    }

    public Float getKeyFloatValue(String mKey, int mDefValue) {
        Float mFloat = null;
        if (mSharePrefs != null) {
            mFloat = mSharePrefs.getFloat(mKey, mDefValue);
        }
        return mFloat;
    }

    public long getKeyLongValue(String mKey, long mDefValue) {
        long mFloat = 0L;
        if (mSharePrefs != null) {
            mFloat = mSharePrefs.getLong(mKey, mDefValue);
        }
        return mFloat;
    }

    /**
     * 保存整型的键值对到配置文件当中.
     *
     * @param mKey
     * @param mValue
     * @Title: saveKey
     * @Description: TODO
     * @return: void
     */
    public void saveKey(String mKey, int mValue) {
        if (mSharePrefs != null) {
            editor = mSharePrefs.edit();
            editor.putInt(mKey, mValue);
            editor.commit();
        }
    }

    /**
     * 保存boolean类型的键值对到配置文件当中.
     *
     * @param mKey
     * @param mValue
     * @Title: saveKey
     * @Description: TODO
     * @return: void
     */
    public void saveKey(String mKey, boolean mValue) {
        if (mSharePrefs != null) {
            editor = mSharePrefs.edit();
            editor.putBoolean(mKey, mValue);
            editor.commit();
        }
    }

    /**
     * 保存float类型的键值对到配置文件当中.
     *
     * @param mKey
     * @param mValue
     * @Title: saveKey
     * @Description: TODO
     * @return: void
     */
    public void saveKey(String mKey, Float mValue) {
        if (mSharePrefs != null) {
            editor = mSharePrefs.edit();
            editor.putFloat(mKey, mValue);
            editor.commit();
        }
    }

    /**
     * 保存long类型的键值对到配置文件当中
     *
     * @param mKey
     * @param mValue
     * @Title: saveKey
     * @Description: TODO
     * @return: void
     */
    public void saveKey(String mKey, long mValue) {
        if (mSharePrefs != null) {
            editor = mSharePrefs.edit();
            editor.putLong(mKey, mValue);
            editor.commit();
        }
    }

    public void removeKey(String key) {
        if (mSharePrefs != null) {
            editor = mSharePrefs.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        sInstance.close();
        super.finalize();
    }

    /**
     * 清除 SharedPreferences 文件的所有数据， 注意 在ondestory 方法中使用，不要再同一个页面中使用
     * 在同一个页面中不能销毁所有的数据
     *
     * @Title: clearALlData
     * @Description: TODO
     * @return: void
     */
    public void clearALlData() {
        if (mSharePrefs != null) {
            mSharePrefs.edit().clear().commit();
        }
    }


    /**
     * 写/data/data/<应用程序名>目录上的文件
     *
     * @param file
     * @param object
     * @return
     */
    public String saveDataToLocate(String file, Object object) {
        String res = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Activity.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (FileNotFoundException e) {
            res = "没有找到文件";
            e.printStackTrace();
        } catch (IOException e) {
            res = "没有数据";
            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
                res = "保存成功";
            } catch (IOException e) {
                res = "没有数据";
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 读/data/data/<应用程序名>目录上的文件
     *
     * @param file
     * @return
     */
    public Object getloadDataFromLocate(String file) {
        Object obj = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    /**
     * 删除本地存储文件
     *
     * @param file
     */
    public void deleteDataFromLocate(String file) {
        File f = new File(context.getFilesDir(), file);
        if (f.exists()) {
            f.delete();
        }
    }


}
