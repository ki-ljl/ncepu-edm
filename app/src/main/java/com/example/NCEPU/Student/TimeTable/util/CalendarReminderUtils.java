package com.example.NCEPU.Student.TimeTable.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarReminderUtils {
    private static final String CALENDER_URL = "content://com.android.calendar/calendars";
    private static final String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static final String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static final String CALENDARS_NAME = "LightTimetable";
    private static final String CALENDARS_ACCOUNT_NAME = "LightTimetable@test.com";
    private static final String CALENDARS_ACCOUNT_TYPE = "com.android.LightTimetable";
    private static final String CALENDARS_DISPLAY_NAME = "LightTimetable账户";

    public static final String DESCRIPTION = "小轻课程表自动创建";

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    public static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        //  日历名称
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        //  日历账号，为邮箱格式
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        //  账户类型，com.android.exchange
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        //  展示给用户的日历名称
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        //  它是一个表示被选中日历是否要被展示的值。
        //  0值表示关联这个日历的事件不应该展示出来。
        //  而1值则表示关联这个日历的事件应该被展示出来。
        //  这个值会影响CalendarContract.instances表中的生成行。
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        //  账户标记颜色
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        //  账户级别
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        //  它是一个表示日历是否应该被同步和是否应该把它的事件保存到设备上的值。
        //  0值表示不要同步这个日历或者不要把它的事件存储到设备上。
        //  1值则表示要同步这个日历的事件并把它的事件储存到设备上。
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        //  时区
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        //  账户拥有者
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        return result == null ? -1 : ContentUris.parseId(result);
    }

    /**
     * @param context
     * @param title          日程内容
     * @param description    日程备注
     * @param location       地点
     * @param eventStartTime 日程开始时间
     * @param eventLength    日程时长
     * @return 是否添加成功
     */
    public static Uri addCalendarEvent(Context context,
                                       String title,
                                       String description,
                                       String location,
                                       long eventStartTime,
                                       long eventLength) {
        if (context == null) {
            return null;
        }
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return null;
        }
        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(eventStartTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + eventLength);//设置终止时间,开始时间+日程时长
        long end = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.DESCRIPTION, description);
        event.put(CalendarContract.Events.CALENDAR_ID, calId); //插入账户的id
        //添加日程地点
        event.put(CalendarContract.Events.EVENT_LOCATION, location);
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());//这个是时区，必须有
        return context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件

    }

    /**
     * @param context
     * @param event          日程事件uri
     * @param previousMinute 提前previousMinute分钟提醒
     * @return
     */
    public static Uri addCalendarAlarm(Context context, Uri event, int previousMinute) {
        if (event == null)
            return null;
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(event));
        values.put(CalendarContract.Reminders.MINUTES, previousMinute);// 提前previousMinute分钟提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        return context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);

    }

    /**
     * @param context
     * @param description 备注
     * @param start       事件开始时间
     * @param end         事件结束时间
     * @return
     */
    public static int findCalendarEvent(Context context, String description, long start, long end) {
        if (context == null) {
            return -1;
        }
        Cursor eventCursor = context.getContentResolver()
                .query(Uri.parse(CALENDER_EVENT_URL), new String[]{CalendarContract.Events._ID},
                        CalendarContract.Events.DESCRIPTION + "=? and " +
                                CalendarContract.Events.DTSTART + ">? and " +
                                CalendarContract.Events.DTEND + "<?",
                        new String[]{description, String.valueOf(start), String.valueOf(end)}, null);

        try {
            if (eventCursor != null) { //查询返回空值
                return eventCursor.getCount();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return -1;
    }

    /**
     * @param context
     * @param description 日程备注
     * @return
     */
    public static boolean deleteCalendarEvent(Context context, String description) {
        int rows = context.getContentResolver().
                delete(Uri.parse(CALENDER_EVENT_URL),
                        CalendarContract.Events.DESCRIPTION + "=?",
                        new String[]{description});
        return rows != -1; //事件删除失败
    }

    /**
     * 检查是否具有日程添加权限
     *
     * @param activity
     * @return
     */
    public static boolean checkPermission(Activity activity) {
        try {
            int checkPermission = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.WRITE_CALENDAR);
            return checkPermission == PackageManager.PERMISSION_GRANTED;

        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 动态获取权限
     *
     * @param activity
     * @param requestCode
     */
    public static void fetchPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR}, requestCode);
    }
}
