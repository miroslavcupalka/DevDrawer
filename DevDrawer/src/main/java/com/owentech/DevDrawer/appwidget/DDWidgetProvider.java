package com.owentech.DevDrawer.appwidget;

/**
 * Created with IntelliJ IDEA.
 * User: owent
 * Date: 25/01/2013
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.activities.ClickHandlingActivity;
import com.owentech.DevDrawer.utils.Database;

public class DDWidgetProvider extends AppWidgetProvider {

    public static String PACKAGE_STRING = "default.package";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews widget = getRemoteViews(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static RemoteViews getRemoteViews(Context context, int appWidgetId) {
        // Setup the widget, and data source / adapter
        Intent svcIntent = new Intent(context, DDWidgetService.class);

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int widgetLayoutResId = sp.getString("theme", "Light").equals("Light") ? R.layout.widget_layout : R.layout.widget_layout_dark;
        RemoteViews widget = new RemoteViews(context.getPackageName(), widgetLayoutResId);
        widget.setRemoteAdapter(R.id.listView, svcIntent);

        Intent clickIntent = new Intent(context, ClickHandlingActivity.class);
        PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String name = new Database(context).getWidgetNames().get(appWidgetId);

        if (name == null || name.trim().isEmpty()) {
            widget.setViewVisibility(R.id.widget_layout_titletv, View.GONE);
            widget.setViewVisibility(R.id.widget_layout_titledivider, View.GONE);
        } else {
            widget.setViewVisibility(R.id.widget_layout_titletv, View.VISIBLE);
            widget.setViewVisibility(R.id.widget_layout_titledivider, View.VISIBLE);
            widget.setTextViewText(R.id.widget_layout_titletv, name);
        }
        widget.setPendingIntentTemplate(R.id.listView, clickPI);
        return widget;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            new Database(context).removeWidgetFromDatabase(appWidgetId);
        }
    }
}