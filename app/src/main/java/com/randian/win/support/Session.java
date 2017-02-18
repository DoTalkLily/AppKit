package com.randian.win.support;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * session control
 */
public class Session {

    private String userId;
    private String accessToken;

    public Session(String userId) {
        this.userId = userId;
        this.accessToken = "";
    }

    public static Session get(Context context) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        String currentUserId = pref.getString("randian_current_user_id", "0");
        if (!"0".equals(currentUserId)) {
            return get(context, currentUserId);
        } else {
            return null;
        }
    }

    private Session(final String id, final String token) {
        userId = id;
        accessToken = token;
    }

    public static Session get(Context context, String userId) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        if (isUserIdExists(context, userId)) {
            Session s = new Session(userId,
                    pref.getString(getCurrentKey(userId, "access_token"),""));
            if (s.accessToken.equals("")) {
                return null;
            }
            return s;
        } else {
            return null;
        }
    }

    public static Set<Long> getUserIds(Context context) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        String userIds = pref.getString("randian_user_ids", null);

        Set<Long> ids = new HashSet<Long>();
        if (userIds != null) {
            String[] strings = userIds.split(",");
            for (String s : strings) {
                if (!s.trim().equals("")) {
                    ids.add(Long.parseLong(s));
                }

            }
        }

        return ids;
    }

    private static boolean isUserIdExists(Context context, String userId) {
        Set<Long> ids = getUserIds(context);
        return ids.contains(userId);
    }

    private static Set<Long> deleteUserid(Context context, String userId) {
        Set<Long> ids = getUserIds(context);
        ids.remove(userId);

        StringBuilder sb = new StringBuilder();
        for (long id : ids) {
            sb.append(id);
            sb.append(",");
        }
        String s = sb.toString();
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("randian_user_ids", s);
        edit.commit();
        return ids;
    }

    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear().commit();
    }

    public void save(Context context, boolean setCurrent) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(getCurrentKey(this.userId, "access_token"), this.accessToken);

        String ids = pref.getString("randian_user_ids", "");
        boolean exist = false;
        if (!ids.equals("")) {
            String[] id = ids.split(",");
            for (String s : id) {
                if (s.equalsIgnoreCase(String.valueOf(this.userId))) {
                    exist = true;
                    break;
                }
            }
        }
        if (!exist) {
            if (ids.equals("")) {
                ids = String.valueOf(this.userId);
            } else {
                ids = ids + "," + this.userId;
            }
            edit.putString("randian_user_ids", ids);
        }
        if (setCurrent) {
            edit.putString("randian_current_user_id", this.userId);
        }
        edit.commit();
    }

    public void save(Context context) {
        save(context, true);
    }

    public void delete(Context context) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.remove(getCurrentKey(this.userId, "access_token"));
        if (pref.getString("randian_current_user_id", "0") == this.userId) {
            edit.remove("randian_current_user_id");
        }
        deleteUserid(context, this.userId);
        edit.commit();
    }

    @Override
    public String toString() {
        return "Session{" +
                "userId=" + userId +
                ", accessToken='" + accessToken +
                '}';
    }

    private static String getCurrentKey(String uid, String key) {
        String format = "%d_%s";
        return String.format(format, uid, key);
    }

    public static void setCurrentUser(Context context, long currentUserId) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        SharedPreferences.Editor edit = pref.edit();
        if (getUserIds(context).contains(currentUserId)) {
            edit.putLong("randian_current_user_id", currentUserId);
        } else {
            throw new IllegalArgumentException("it's not contain use " + currentUserId);
        }
        edit.commit();
    }

    public static void clear(Context context, String currentUserId) {
        SharedPreferences pref = context.getSharedPreferences("randian_session", 0);
        SharedPreferences.Editor edit = pref.edit();
        if (pref.getString("randian_current_user_id", "0") == currentUserId) {
            edit.remove("randian_current_user_id");
        }
        edit.remove(getCurrentKey(currentUserId, "access_token"));
        edit.commit();
        deleteUserid(context, currentUserId);
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
