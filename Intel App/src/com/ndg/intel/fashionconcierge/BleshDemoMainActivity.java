package com.ndg.intel.fashionconcierge;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.blesh.sdk.classes.Blesh;
import com.blesh.sdk.classes.BleshInstance;
import com.blesh.sdk.models.BleshTemplateResult;

public class BleshDemoMainActivity extends Activity implements LoginFragment.OnFragmentInteractionListener {

    private final String TAG = "BleshMainActivity";

    private static final String LOGIN_FRAGMENT_TAG = "LoginFragmentTag";

    public static final String PACKAGE_NAME = "com.ndg.intel.fashionconcierge";
    public static final String INTEGRATION_ID = "integrationId";

    private static boolean enableLogout = false;

    private static final int REQUEST_ENABLE_BT = 0;

    private static final String LOGIN_STATE = "LOGIN_STATE";

    private ImageView backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "mainActivity called!" + " SDK_INT:"
                + android.os.Build.VERSION.SDK_INT + " CODENAME:"
                + android.os.Build.VERSION.CODENAME + " RELEASE:"
                + android.os.Build.VERSION.RELEASE);

        backgroundView = (ImageView) findViewById(R.id.background_image);

        enableLogout = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE).getBoolean(LOGIN_STATE, false);

        Log.i(TAG, "Login state:" + enableLogout);

        if (savedInstanceState == null && !enableLogout) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_and_slide_up_fragment, R.anim.fade_out_fragment, R.anim.fade_in_activity, R.anim.fade_out_fragment)
                    .add(R.id.blesh_main_container, new LoginFragment(), LOGIN_FRAGMENT_TAG)
                    .commit();
        }
        else
        {
            final String integrationId = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE).getString(INTEGRATION_ID, "");
            backgroundView.setImageResource(R.drawable.intel_login);
            Toast.makeText(this, "Logged in with userId:" + integrationId, Toast.LENGTH_LONG).show();

        }

        // Define a callback reference to be used by the Blesh service
        // in order to push the user's action results to your application
        BleshTemplateResult result = new BleshTemplateResult() {

            @Override
            public void bleshTemplateResultCallback(String actionType,
                                                    String actionValue) {

                Log.w(TAG, "bleshTemplateResultCallback:" + actionType + " value:"
                        + actionValue);

                if (actionType != null && actionValue != null) {
                    Log.i(TAG, "I received type:" + actionType + " value:"
                            + actionValue);

                    // Check for the action type and value you want to use
                    // You may wish to load a web page here using the action
                    // value

                    // Do something with other action types?
                    // It's completely customizable for your needs!
                } else {
                    Log.i(TAG, "bleshTemplateResultCallback() result is empty!");
                }

            }
        };

        // Register your callback function, named as "result" for this example
        BleshInstance.sharedInstance().setTemplateResult(result);

        ensureBluetoothIsEnabled();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Ensures Bluetooth is available on the beacon and it is enabled. If not,
     * displays a dialog requesting user permission to enable Bluetooth.
     */
    private void ensureBluetoothIsEnabled() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);

        Log.i(TAG, "onPrepareOptionsMenu " + enableLogout);

        menu.findItem(R.id.action_logout).setVisible(enableLogout);
        invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If the configuration menu item was selected
            case R.id.action_logout:
                showLoginFragment(enableLogout);
                enableLogout = false;
                getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGIN_STATE, false).commit();
                getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE).edit().putString(BleshDemoMainActivity.INTEGRATION_ID, "").commit();
                backgroundView.setImageResource(R.drawable.intel_logo);

                startBlesh("invalid");
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoginFragment(boolean isLogout) {

        Toast.makeText(this, "Enter new userId!", Toast.LENGTH_LONG).show();

        if(isLogout) {

            Fragment loginFragment = getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);
            // If the fragment does not exist
            if (loginFragment == null) {

                // Create the fragment
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_and_slide_up_fragment, R.anim.fade_out_fragment, R.anim.fade_in_activity, R.anim.fade_out_fragment)
                        .replace(R.id.blesh_main_container, LoginFragment.newInstance(null), LOGIN_FRAGMENT_TAG)
                        .commit();
                // If the fragment does exist
            } else {
                Log.i(TAG, "loginFragment is not null. visibility:" + loginFragment.isVisible());

                // If the fragment is not currently visible
                if (!loginFragment.isVisible()) {
                    // Assume another fragment is visible, so pop that fragment off the stack
                    getFragmentManager().popBackStack();
                }
            }
        }
        else {


            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_and_slide_up_fragment, R.anim.fade_out_fragment, R.anim.fade_in_activity, R.anim.fade_out_fragment)
                    .replace(R.id.blesh_main_container, LoginFragment.newInstance(null))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void startBlesh(String phone_number) {
        // You can initialize Blesh anywhere you want within your application
        // context

        Log.i(TAG, "startBlesh called with number:" + phone_number);

        // Customer specific initialization parameters
        Intent blesh = new Intent(this, Blesh.class);
        blesh.putExtra("APIUser", "intel");
        blesh.putExtra("APIKey", "RjKqGrNxEs");
        blesh.putExtra("integrationType", "M");
        blesh.putExtra("integrationId", phone_number);
        blesh.putExtra("pushToken", "");
        blesh.putExtra("optionalKey", phone_number);

        /*
         * optionalKey ve integrationId should be String
         * optionalKey is not mandatory while integrationId is
         */

        // Start the Blesh service using the bundle you have just created
        startService(blesh);
    }

    @Override
    public void onFragmentInteraction(String number) {

        startBlesh(number);

        Log.i(TAG, "IntegrationID saving number in sp" + number);

        SharedPreferences sp = this.getSharedPreferences(BleshDemoMainActivity.PACKAGE_NAME, Context.MODE_PRIVATE);

        sp.edit().putString(BleshDemoMainActivity.INTEGRATION_ID, number).commit();
        sp.edit().putBoolean(LOGIN_STATE, true).commit();
        enableLogout = true;

        Fragment loginFragment = getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);

        if(loginFragment != null) {
            getFragmentManager().beginTransaction().remove(loginFragment)
                    .setCustomAnimations(R.anim.fade_in_and_slide_up_fragment, R.anim.fade_out_fragment, R.anim.fade_in_activity, R.anim.fade_out_fragment)
                    .commit();
        }

        backgroundView.setImageResource(R.drawable.intel_login);
        backgroundView.invalidate();

        Toast.makeText(this, "Logged in with userId:" + number, Toast.LENGTH_LONG).show();

    }

}
