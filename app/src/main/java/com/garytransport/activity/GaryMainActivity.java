package com.garytransport.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.garytransport.R;
import com.garytransport.service.LocationService;
import com.rilixtech.CountryCodePicker;

import am.appwise.components.ni.NoInternetDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class GaryMainActivity extends AppCompatActivity {


    private CountryCodePicker ccp;
    private AppCompatEditText edMobileNo;
    private Button btnSubmit;
    private boolean mIsServiceStarted = false;
    private BroadcastReceiver mMessageReceiver;
    private RelativeLayout parentLayout;
    private NoInternetDialog noInternetDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garry_main);


        GaryMainActivityPermissionsDispatcher.locationPermissionsWithPermissionCheck(this);

        noInternetDialog = new NoInternetDialog.Builder(GaryMainActivity.this).build();


        ccp =  findViewById(R.id.ccp);
        edMobileNo =  findViewById(R.id.edMobileNo);
        btnSubmit =  findViewById(R.id.btnSubmit);
        parentLayout =  findViewById(R.id.parentLayout);


        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (edMobileNo.isFocused()) {
                        Rect outRect = new Rect();
                        edMobileNo.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            edMobileNo.clearFocus();
                            edMobileNo.setText("");
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });




        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



                Bundle b = intent.getBundleExtra("Location");
                Location lastKnownLoc =  b.getParcelable("Location");


                if (lastKnownLoc != null) {

                    Toast.makeText(GaryMainActivity.this, "Latitude: =" + lastKnownLoc.getLatitude() + " Longitude:=" + lastKnownLoc.getLongitude(),
                            Toast.LENGTH_SHORT).show();
                }


            }
        };

        LocalBroadcastManager.getInstance(GaryMainActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("GPSLocationUpdates"));



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ccp.registerPhoneNumberTextView(edMobileNo);

                String codeWithPh = ccp.getFullNumberWithPlus();


                if(TextUtils.isEmpty(edMobileNo.getText().toString())){
                    edMobileNo.setError("Enter mobile no.");
                }
                else {

                    Toast.makeText(GaryMainActivity.this,codeWithPh,Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });





    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationPermissions() {


        if (!mIsServiceStarted)
        {
            mIsServiceStarted = true;
            startService(new Intent(this, LocationService.class));
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GaryMainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showRationaleforLocation(final PermissionRequest request) {
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLoctionDenied() {
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationNeverAsk() {
    }
}
