package com.owentech.DevDrawer.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.data.model.ApplicationDataHolder;
import com.owentech.DevDrawer.utils.AppConstants;
import com.owentech.DevDrawer.utils.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DDWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context = null;
    private int appWidgetId;

    PackageManager pm;

    private List<ApplicationDataHolder> applicationDataHolders;

    public DDWidgetViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        onDataSetChanged();
    }

    @Override
    public void onCreate() {
        // Nothing yet
    }

    @Override
    public void onDestroy() {
        // Nothing yet
    }

    @Override
    public int getCount() {
        return applicationDataHolders.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean rootClearData = sp.getBoolean("rootClearData", false);

        // Setup the list item and intents for on click
        RemoteViews row = new RemoteViews(context.getPackageName(), rootClearData ? R.layout.list_item_more : R.layout.list_item);

        try {
            row.setTextViewText(R.id.packageNameTextView, applicationDataHolders.get(position).packageName);
            row.setTextViewText(R.id.appNameTextView, applicationDataHolders.get(position).name);
            row.setImageViewBitmap(R.id.imageView, convertFromDrawable(applicationDataHolders.get(position).icon));
            row.setViewVisibility(R.id.clearImageButton, rootClearData ? View.VISIBLE : View.GONE);

            Intent appDetailsClickIntent = new Intent();
            Bundle appDetailsClickExtras = new Bundle();
            //appDetailsClickExtras.putBoolean("appDetails", true);
            appDetailsClickExtras.putInt("launchType", AppConstants.LAUNCH_APP_DETAILS);
            appDetailsClickExtras.putString(DDWidgetProvider.PACKAGE_STRING, applicationDataHolders.get(position).packageName);
            appDetailsClickIntent.putExtras(appDetailsClickExtras);
            row.setOnClickFillInIntent(R.id.appDetailsImageButton, appDetailsClickIntent);

            Intent uninstallClickIntent = new Intent();
            Bundle uninstallClickExtras = new Bundle();
            //appDetailsClickExtras.putBoolean("appDetails", true);
            uninstallClickExtras.putInt("launchType", AppConstants.LAUNCH_UNINSTALL);
            uninstallClickExtras.putString(DDWidgetProvider.PACKAGE_STRING, applicationDataHolders.get(position).packageName);
            uninstallClickIntent.putExtras(uninstallClickExtras);
            row.setOnClickFillInIntent(R.id.uninstallImageButton, uninstallClickIntent);

            Intent clearClickIntent = new Intent();
            Bundle clearClickExtras = new Bundle();
            clearClickExtras.putInt("launchType", AppConstants.LAUNCH_CLEAR);
            clearClickExtras.putString(DDWidgetProvider.PACKAGE_STRING, applicationDataHolders.get(position).packageName);
            clearClickIntent.putExtras(clearClickExtras);
            row.setOnClickFillInIntent(R.id.clearImageButton, clearClickIntent);

            Intent moreClickIntent = new Intent();
            Bundle moreClickExtras = new Bundle();
            moreClickExtras.putInt("launchType", AppConstants.LAUNCH_MORE);
            moreClickExtras.putString(DDWidgetProvider.PACKAGE_STRING, applicationDataHolders.get(position).packageName);
            moreClickIntent.putExtras(moreClickExtras);
            row.setOnClickFillInIntent(R.id.moreImageButton, moreClickIntent);

            Intent rowClickIntent = new Intent();
            Bundle rowClickExtras = new Bundle();
            //rowClickExtras.putBoolean("appDetails", false);
            rowClickExtras.putInt("launchType", AppConstants.LAUNCH_APP);
            rowClickExtras.putString(DDWidgetProvider.PACKAGE_STRING, applicationDataHolders.get(position).packageName);
            rowClickIntent.putExtras(rowClickExtras);
            row.setOnClickFillInIntent(R.id.touchArea, rowClickIntent);

            return (row);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return (null);
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }

    @Override
    public void onDataSetChanged() {
        // Update the dataset
        getApps();
    }

    // Method to get all apps from the app database and add to the dataset
    private void getApps() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Get all apps from the app table for this widget
        String[] packages = Database.getInstance(context).getAllAppsInDatabase(appWidgetId);
        pm = context.getPackageManager();

        // Defensive code, was getting some strange behaviour and forcing the lists seems to fix
        applicationDataHolders = null;

        // Setup the lists holding the data
        applicationDataHolders = new ArrayList<>();

        // Loop though adding details from PackageManager to the lists
        for (String s : packages) {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            ApplicationDataHolder applicationDataHolder;

            try {
                packageInfo = pm.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
                applicationInfo = packageInfo.applicationInfo;

                applicationDataHolder = new ApplicationDataHolder();
                applicationDataHolder.name = applicationInfo.loadLabel(pm).toString();
                applicationDataHolder.packageName = applicationInfo.packageName;
                applicationDataHolder.icon = applicationInfo.loadIcon(pm);
                applicationDataHolder.firstInstalled = packageInfo.firstInstallTime;
                applicationDataHolder.lastUpdate = packageInfo.lastUpdateTime;
                applicationDataHolder.versionName = packageInfo.versionName;
                applicationDataHolder.versionCode = packageInfo.versionCode;
                applicationDataHolders.add(applicationDataHolder);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(applicationDataHolders, sp.getString("widgetSorting", AppConstants.ORDER_ORIGINAL).equalsIgnoreCase(AppConstants.ORDER_ORIGINAL) ? ApplicationDataHolder.SORT_BY_LAST_UPDATE_ASC :
                ApplicationDataHolder.SORT_BY_PACKAGE_NAME_ASC);
    }

    // Method to return a bitmap from drawable
    public Bitmap convertFromDrawable(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return getBitmapFromDrawable(drawable);
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
}
