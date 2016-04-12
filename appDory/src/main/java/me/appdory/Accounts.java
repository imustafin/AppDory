package me.appdory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import com.perm.kate.api.Api;
import com.perm.kate.api.KException;
import com.perm.kate.api.User;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Accounts {

    public static class VkAccount {

        public static final String VK_APPID = "4929453";

        static VkAccount instance = null;

        public static synchronized VkAccount getInstance() {
            if (instance == null) {
                throw new IllegalStateException("No instance created yet");
            }
            return instance;
        }

        static synchronized VkAccount setNewInstance(String accessToken,
                                                     long userId) {
            instance = new VkAccount(accessToken, userId);
            return getInstance();
        }

        VkAccount(String accessToken, long userId) {
            this.accessToken = accessToken;
            this.userId = userId;
            this.api = new Api(accessToken, VK_APPID);
        }

        public synchronized void loadNameSurnamePhoto() throws IOException, JSONException,
                KException {
            User me = api.getProfiles(Arrays.asList(userId), null, "photo_200", null, null, null).get(0);
            this.name = me.first_name;
            this.surname = me.last_name;
            this.photo200 = me.photo_200;
        }

        Bitmap loadPhotoBitmap() throws IOException {
            URL url = new URL(photo200);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        }


        static final String SHARED_PREFERENCES_ACCESS_TOKEN = "vk_access_token";
        static final String SHARED_PREFERENCES_USER_ID = "vk_user_id";

        public String accessToken = null;
        public long userId;
        public Api api;

        public String name;
        public String surname;

        String photo200;

        public synchronized static void logout() {
            instance = null;
        }

        public static synchronized void removeFromSharedPreferences(
                Context context) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            Editor editor = prefs.edit();
            editor.remove(SHARED_PREFERENCES_ACCESS_TOKEN);
            editor.remove(SHARED_PREFERENCES_USER_ID);
            editor.commit();
        }

        public synchronized void saveToSharedPreferences(Context context) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            Editor editor = prefs.edit();
            editor.putString(SHARED_PREFERENCES_ACCESS_TOKEN, accessToken);
            editor.putLong(SHARED_PREFERENCES_USER_ID, userId);
            editor.commit();
        }

        public static synchronized void readFromSharedPreferences(
                Context context) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String accessToken = prefs.getString(
                    SHARED_PREFERENCES_ACCESS_TOKEN, null);
            long userId = prefs.getLong(SHARED_PREFERENCES_USER_ID, 0);
            if (accessToken == null) {
                instance = null;
            } else {
                instance = new VkAccount(accessToken, userId);
            }
        }

        public static synchronized boolean gotInstance() {
            return instance != null;
        }

    }

}
