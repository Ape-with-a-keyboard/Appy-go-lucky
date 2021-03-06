/*
AppyGoLucky: Simple application that allows for those who need rides to and from locations to
connect with a driver who could take them. This application permits for simple coordination,
payment agreement, and route agreement.

Copyright (C) 2016 J Maxwell Douglas jmdougla@ualberta.ca, Eric Hsueh, Corey Hunt, Vinson Lai

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

package com.example.ehsueh.appygolucky;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/**
 * This is the main activity of the app, it loads the Homepage and just verifies that a network
 * connection is available.
 *
 */
public class MainActivity extends ActionBarActivity {
    UserController uc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load from file, and open the home page if user already logged in
        uc = new UserController(getApplicationContext());
        uc.loadFromFile();
        if(uc.getCurrentUser() != null) {
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void LoginClick(View view) {
        EditText usernameEditTxt = (EditText) findViewById(R.id.usernameInputTxt);
        final String desiredUsername = usernameEditTxt.getText().toString();

        //Only continue if the username field contains text
        if(desiredUsername.equals("")) {
            Toast toast = Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Logging you in...",
                    Toast.LENGTH_SHORT);
            toast.show();
            //Create a GetUsersByUsernameTask which includes an anonymous listener class to act on the data that
            //is retrieved from the server
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
                                        if (results.size() != 0) {
                                            User newUser = (User) results.get(0);
                                            //Added a "download complete" toast to this method
                                            uc.existingUserLogin(newUser);

                                            Intent intent = new Intent(getApplicationContext(),
                                                    HomePageActivity.class);
                                            startActivity(intent);
                                        }
                                        //Else allow the user to create a new profile
                                        else {
                                            try {
                                                //Create a user with the desired username, and send them to
                                                //the edit profile activity
                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                        "No user with that username, creating new user.",Toast.LENGTH_SHORT);
                                                toast.show();
                                                uc.newUserLogin(desiredUsername, "", "", "", "");
                                                Intent intent = new Intent(getApplicationContext(),
                                                        EditProfileActivity.class);
                                                intent.putExtra("isNew",true);
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                        "We've experienced a problem with the server. " +
                                                                "Please" +
                                                                "try again", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        }
                                    }
                                }
                            });
            getUsersByUsernameTask.execute(desiredUsername);

        }


    }

//    I don't think we need the three dot on main screen
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
