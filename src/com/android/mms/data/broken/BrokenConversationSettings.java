package com.android.mms.data.broken;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.mms.ui.MessagingPreferenceActivity;

public class BrokenConversationSettings {
    private static final String TAG = "BrokenConversationSettings";

    private Context mContext;
    /* package */
    long mThreadId;
    int mNotificationEnabled;
    String mNotificationTone;
    int mVibrateEnabled;
    String mVibratePattern;

    private static final int DEFAULT_NOTIFICATION_ENABLED = BrokenMmsDatabaseHelper.DEFAULT;
    private static final String DEFAULT_NOTIFICATION_TONE = "";
    private static final int DEFAULT_VIBRATE_ENABLED = BrokenMmsDatabaseHelper.DEFAULT;
    private static final String DEFAULT_VIBRATE_PATTERN = "";

    private BrokenConversationSettings(Context context, long threadId, int notificationEnabled,
        String notificationTone, int vibrateEnabled, String vibratePattern) {
        mContext = context;
        mThreadId = threadId;
        mNotificationEnabled = notificationEnabled;
        mNotificationTone = notificationTone;
        mVibrateEnabled = vibrateEnabled;
        mVibratePattern = vibratePattern;
    }

    public static BrokenConversationSettings getOrNew(Context context, long threadId) {
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(BrokenMmsDatabaseHelper.CONVERSATIONS_TABLE,
            BrokenMmsDatabaseHelper.CONVERSATIONS_COLUMNS,
            " thread_id = ?",
            new String[] { String.valueOf(threadId) },
            null, null, null, null);

        // we should only have one result
        int count = cursor.getCount();
        BrokenConversationSettings convSetting;
        if (cursor != null && count == 1) {
            cursor.moveToFirst();
            convSetting = new BrokenConversationSettings(context,
                threadId,
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4)
            );
        } else if (count > 1) {
            Log.wtf(TAG, "More than one settings with the same thread id is returned!");
            return null;
        } else {
            convSetting = new BrokenConversationSettings(context, threadId,
                DEFAULT_NOTIFICATION_ENABLED, DEFAULT_NOTIFICATION_TONE,
                DEFAULT_VIBRATE_ENABLED, DEFAULT_VIBRATE_PATTERN);

            helper.insertBrokenConversationSettings(convSetting);
        }

        return convSetting;
    }

    public static void delete(Context context, long threadId) {
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(context);
        helper.deleteBrokenConversationSettings(threadId);
    }

    public static void deleteAll(Context context) {
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(context);
        helper.deleteAllBrokenConversationSettings();
    }

    public long getThreadId() {
        return mThreadId;
    }

    public boolean getNotificationEnabled() {
        if (mNotificationEnabled == BrokenMmsDatabaseHelper.DEFAULT) {
            SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
            return sharedPreferences.getBoolean(MessagingPreferenceActivity.NOTIFICATION_ENABLED,
                DEFAULT_NOTIFICATION_ENABLED == BrokenMmsDatabaseHelper.TRUE);
        }
        return mNotificationEnabled == BrokenMmsDatabaseHelper.TRUE;
    }

    public void setNotificationEnabled(boolean enabled) {
        mNotificationEnabled = enabled ? BrokenMmsDatabaseHelper.TRUE : BrokenMmsDatabaseHelper.FALSE;
        setNotificationEnabled(mNotificationEnabled);
    }

    public void setNotificationEnabled(int enabled) {
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(mContext);
        helper.updateBrokenConversationSettingsField(mThreadId,
            BrokenMmsDatabaseHelper.CONVERSATIONS_NOTIFICATION_ENABLED, enabled);
    }

    public String getNotificationTone() {
        if (mNotificationTone.equals("")) {
            SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
            return sharedPreferences.getString(MessagingPreferenceActivity.NOTIFICATION_RINGTONE,
                null);
        }
        return mNotificationTone;
    }

    public void setNotificationTone(String tone) {
        mNotificationTone = tone;
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(mContext);
        helper.updateBrokenConversationSettingsField(mThreadId,
            BrokenMmsDatabaseHelper.CONVERSATIONS_NOTIFICATION_TONE, tone);
    }

    public boolean getVibrateEnabled() {
        if (mVibrateEnabled == BrokenMmsDatabaseHelper.DEFAULT) {
            SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
            return sharedPreferences.getBoolean(MessagingPreferenceActivity.NOTIFICATION_VIBRATE,
                DEFAULT_VIBRATE_ENABLED == BrokenMmsDatabaseHelper.TRUE);
        }
        return mVibrateEnabled == BrokenMmsDatabaseHelper.TRUE;
    }

    public void setVibrateEnabled(boolean enabled) {
        mVibrateEnabled = enabled ? BrokenMmsDatabaseHelper.TRUE : BrokenMmsDatabaseHelper.FALSE;
        setVibrateEnabled(mVibrateEnabled);
    }

    public void setVibrateEnabled(int enabled) {
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(mContext);
        helper.updateBrokenConversationSettingsField(mThreadId,
            BrokenMmsDatabaseHelper.CONVERSATIONS_VIBRATE_ENABLED, enabled);
    }

    public String getVibratePattern() {
        if (mVibratePattern.equals("")) {
            SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
            return sharedPreferences.getString(MessagingPreferenceActivity.NOTIFICATION_VIBRATE_PATTERN,
                "0,1200");
        }
        return mVibratePattern;
    }

    public void setVibratePattern(String pattern) {
        mVibratePattern = pattern;
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(mContext);
        helper.updateBrokenConversationSettingsField(mThreadId,
            BrokenMmsDatabaseHelper.CONVERSATIONS_VIBRATE_PATTERN, pattern);
    }

    public void resetToDefault() {
        mNotificationEnabled = DEFAULT_NOTIFICATION_ENABLED;
        mNotificationTone = DEFAULT_NOTIFICATION_TONE;
        mVibrateEnabled = DEFAULT_VIBRATE_ENABLED;
        mVibratePattern = DEFAULT_VIBRATE_PATTERN;
        BrokenMmsDatabaseHelper helper = BrokenMmsDatabaseHelper.getInstance(mContext);
        helper.updateBrokenConversationSettings(this);
    }
}
