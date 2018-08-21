package com.android.al3arrab.universalapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.al3arrab.universalapp.MusicPlayer.MyPlayer;

import static com.android.al3arrab.universalapp.MainActivity.songs;

public class NowPlayingWidget extends AppWidgetProvider {

    public static final String UPDATE_SONG_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String EXTRA_ITEM = "com.android.al3arrab.universalapp.EXTRA_ITEM";

    public void onReceive(Context context, Intent intent) {

        if (songs == null){
            return;
        }

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_SONG_ACTION)) {

            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, NowPlayingWidget.class));

            Log.e("received", intent.getAction());

            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_songs);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, ListViewWidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.now_playing_widget);

            rv.setRemoteAdapter(R.id.list_view_songs, intent);

            Intent startActivityIntent = new Intent(context, MyPlayer.class);

            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            rv.setPendingIntentTemplate(R.id.list_view_songs, startActivityPendingIntent);

            rv.setEmptyView(R.id.list_view_songs, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
