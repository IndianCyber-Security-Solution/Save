package com.example.save;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {
    ListView listView;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        // initialise layout
        listView = findViewById(R.id.listview);
        text = findViewById(R.id.totalapp);
    }

    public void getallapps(View view) {
        // get list of all the apps installed
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        String[] apps = new String[packList.size()];
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);

            apps[i] =  "Name:  " +packInfo.applicationInfo.loadLabel(getPackageManager()).toString()+"\n"+"PackageName:  "+ packInfo.packageName+"\nVersion Code:"+packInfo.versionCode;
        }
        // set all the apps name in list view
        listView.setAdapter(new ArrayAdapter<String>(TasksActivity.this, android.R.layout.simple_list_item_1, apps));
        // write total count of apps available.
        text.setText(packList.size() + " Apps are installed");
    }

    /*public void getallapps(View view) {
        // get list of all the apps installed
        List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        // create a list with size of total number of apps
        String[] apps = new String[infos.size()];
        int i = 0;
        // add all the app name in string list
        for (ApplicationInfo info : infos) {

                apps[i] = String.valueOf(info.packageName);
                i++;


            //datadir
            //package
            //processname
            //uid
            //icon
            //sourcedir


        }
        // set all the apps name in list view
        listView.setAdapter(new ArrayAdapter<String>(TasksActivity.this, android.R.layout.simple_list_item_1, apps));
        // write total count of apps available.
        text.setText(infos.size() + " Apps are installed");
    }*/
   /* public void getallapps(View view) throws PackageManager.NameNotFoundException {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // get list of all the apps installed
        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        List<String> componentList = new ArrayList<String>();
        String name = null;
        int i = 0;

        // get size of ril and create a list
        String[] apps = new String[ril.size()];
        for (ResolveInfo ri : ril) {
            if (ri.activityInfo != null) {
                // get package
                Resources res = getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
                // if activity label res is found
                if (ri.activityInfo.labelRes != 0) {
                    name = res.getString(ri.activityInfo.labelRes);
                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(
                            getPackageManager()).toString();
                }
                apps[i] = name;
                i++;
            }
        }
        // set all the apps name in list view
        listView.setAdapter(new ArrayAdapter<String>(TasksActivity.this, android.R.layout.simple_list_item_1, apps));
        // write total count of apps available.
        text.setText(ril.size() + " Apps are installed");
    }*/

    @Override
    protected void onStart() {
        super.onStart();

    }
}