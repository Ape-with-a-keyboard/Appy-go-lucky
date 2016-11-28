package com.example.ehsueh.appygolucky;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the List of drivers to choose from,
 * Contains a List called DriversToChoose
 * and a back button called BackDriverChoose
 * it shows the list of driver that have requested to pick you up
 */

public class ListDriversChooseActivity extends ActionBarActivity {
    private UserController uc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drivers_choose);
        uc = new UserController(getApplicationContext());
        Intent intent = getIntent();
        Integer position = intent.getIntExtra("theRide",0);
        final Ride ride = uc.getRequestedRides().getRides().get(position);
        ListView listView = (ListView) findViewById(R.id.DriversToChoose);

        ArrayList<String> rides = (ArrayList<String>) ride.getDriverUsernames();
        final ArrayList<String> list = new ArrayList<String>(rides);
        final ArrayAdapter rideAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list) {

        };

        listView.setAdapter(rideAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                final int finalPosition = position;
                final String driver = list.get(finalPosition);
                new AlertDialog.Builder(ListDriversChooseActivity.this)
                        .setTitle("Options")
                        .setMessage("What would you like to do?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialogInterface, int i) {
                                ElasticSearchUserController.GetUsersByUsernameTask getUsersByUsernameTask =
                                        new ElasticSearchUserController.GetUsersByUsernameTask(
                                                new ESQueryListener() {
                                                    @Override
                                                    public void onQueryCompletion(List<?> results) {
                                                        //If the result comes back null, it means there was a
                                                        // network error
                                                        if (results == null) {
                                                            Toast toast = Toast.makeText(getApplicationContext(),
                                                                    "We couldn't contact the server.  Please check your " +
                                                                            "connectivity and try again", Toast.LENGTH_SHORT);
                                                            toast.show();
                                                        } else {
                                                            //If that username already exists on the server, we've already downloaded
                                                            // their user object.  Set them to the current user.
                                                            //results holds the user returned from the database.

                                                            User newUser = (User) results.get(0);
                                                            //uc.existingUserLogin(newUser);
                                                            try {
                                                                uc.confirmDriverAcceptance(ride, newUser);
                                                                finish();
                                                            }
                                                            catch (Exception e){
                                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                                        "We've experienced a problem with the server. " +
                                                                                "Please" +
                                                                                "try again", Toast.LENGTH_SHORT);
                                                                toast.show();
                                                            }
                                                        }
                                                    }
                                                });
                                getUsersByUsernameTask.execute(driver);


                            }
                        })
                        .setNeutralButton("Contact Info", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(ListDriversChooseActivity.this, ContactInfoActivity.class);
                                intent.putExtra("driveruser",driver);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .show();
                return false;
            }
        });

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                AlertDialog.Builder adb = new AlertDialog.Builder(ListDriversChooseActivity.this);
//                final int finalPosition = position;
//                final String driver = list.get(finalPosition);
//                adb.setMessage("What would you like to do?");
//                adb.setCancelable(true);
//
//                adb.setPositiveButton("Confirm!", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ElasticSearchUserController.GetUsersByUsernameTask getUsersByUsernameTask =
//                                new ElasticSearchUserController.GetUsersByUsernameTask(
//                                        new ESQueryListener() {
//                                            @Override
//                                            public void onQueryCompletion(List<?> results) {
//                                                //If the result comes back null, it means there was a
//                                                // network error
//                                                if (results == null) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),
//                                                            "We couldn't contact the server.  Please check your " +
//                                                                    "connectivity and try again", Toast.LENGTH_SHORT);
//                                                    toast.show();
//                                                } else {
//                                                    //If that username already exists on the server, we've already downloaded
//                                                    // their user object.  Set them to the current user.
//                                                    //results holds the user returned from the database.
//
//                                                    User newUser = (User) results.get(0);
//                                                    //uc.existingUserLogin(newUser);
//                                                    try {
//                                                        uc.confirmDriverAcceptance(ride, newUser);
//                                                    }
//                                                    catch (Exception e){
//                                                        Toast toast = Toast.makeText(getApplicationContext(),
//                                                                "We've experienced a problem with the server. " +
//                                                                        "Please" +
//                                                                        "try again", Toast.LENGTH_SHORT);
//                                                        toast.show();
//                                                    }
//                                                }
//                                            }
//                                        });
//                        getUsersByUsernameTask.execute(driver);
//
//                    }
//                });
//                adb.setNeutralButton("Contact Info", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        Intent intent = new Intent(ListDriversChooseActivity.this, ContactInfoActivity.class);
//                        intent.putExtra("Driver",driver);
//                        startActivity(intent);
//                    }
//                });
//                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//
//                return false;
//            }
//        });
    }

    public void chooseBack(View view){
        finish();
    }
}
