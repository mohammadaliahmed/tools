package com.appsinventiv.toolsbazzar.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.appsinventiv.toolsbazzar.Activities.LiveChat;
import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Activities.MyOrders;
import com.appsinventiv.toolsbazzar.Activities.ProductComments;
import com.appsinventiv.toolsbazzar.Activities.WholesaleLiveChat;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Seller.SellerMainActivity;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.Orders;
import com.appsinventiv.toolsbazzar.Seller.SellerProductComments;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AliAh on 01/03/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String msg;
    String title, message, type;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String username;
    private String Id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("message payload", "Message data payload: " + remoteMessage.getData());
            msg = "" + remoteMessage.getData();

            Map<String, String> map = remoteMessage.getData();

            message = map.get("Message");
            title = map.get("Title");
            type = map.get("Type");
//            username = map.get("Username");
            Id = map.get("Id");
            handleNow(title, message, type);
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow(msg);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void handleNow(String notificationTitle, String messageBody, String type) {

        int num = (int) System.currentTimeMillis();
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = null;
        if (type.equalsIgnoreCase("RetailChat")) {
            SharedPrefs.setNewMsg("1");
            resultIntent = new Intent(this, LiveChat.class);
            resultIntent.putExtra("username", Id);
        } else if (type.equalsIgnoreCase("WholesaleChat")) {
            int c = Integer.parseInt(SharedPrefs.getNewMsg().equalsIgnoreCase("") ? "0" : SharedPrefs.getNewMsg());
            c = c + 1;
            SharedPrefs.setNewMsg(c + "");

            resultIntent = new Intent(this, WholesaleLiveChat.class);
            resultIntent.putExtra("username", username);
        } else if (type.equalsIgnoreCase("SellerChat")) {
            int c = Integer.parseInt(SharedPrefs.getNewMsg().equalsIgnoreCase("") ? "0" : SharedPrefs.getNewMsg());
            c = c + 1;
            SharedPrefs.setNewMsg(c + "");

            resultIntent = new Intent(this, SellerChats.class);
//            resultIntent.putExtra("username", username);
        } else if (type.equalsIgnoreCase("marketing")) {
            resultIntent = new Intent(this, MainActivity.class);
        } else if (type.equalsIgnoreCase("productRejected")) {
            resultIntent = new Intent(this, SellerMainActivity.class);
        } else if (type.equalsIgnoreCase("order")) {
            resultIntent = new Intent(this, MyOrders.class);
        } else if (type.equals("SellerOrder")) {
            resultIntent = new Intent(this, Orders.class);
        } else if (type.equals("NewComment")) {
            if (SharedPrefs.getVendor() != null) {
                resultIntent = new Intent(this, SellerProductComments.class);
            } else if (SharedPrefs.getCustomerModel() != null) {
                resultIntent = new Intent(this, ProductComments.class);
            }
            resultIntent.putExtra("productId", Id);
            HashMap<String, Double> map = SharedPrefs.getCommentsCount();
            if (map != null && map.size() > 0) {
                if (map.get(Id) == null || map.get(Id) == 0.0) {
                    map.put(Id, 1.0);
                    SharedPrefs.setCommentsCount(map);

                } else {
                    map.put(Id, map.get(Id) + 1.0);
                    SharedPrefs.setCommentsCount(map);
                }
            } else {
                map = new HashMap<>();
                map.put(Id, 1.0);
                SharedPrefs.setCommentsCount(map);

            }
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(notificationTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(num /* Request Code */, mBuilder.build());
    }
}
