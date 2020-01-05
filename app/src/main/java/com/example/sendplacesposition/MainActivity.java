package com.example.sendplacesposition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sendplacesposition.osmmatrix.MatrixResponse;
import com.example.sendplacesposition.osmplaces.OSMPlaces;
import com.example.sendplacesposition.osmplaces.PlacesViewModel;
import com.example.sendplacesposition.osmplaces.SingletonPlacesViewModelFactory;
import com.example.sendplacesposition.osmplaces.cityPlaces;
import com.example.sendplacesposition.retrofit.PlacesServices;
import com.example.sendplacesposition.retrofit.RetrofitClientInstance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;


    private EditText name_place_edit;
    private Button save_place_btn, btnFcmToken;
    private TextView details, osmPlaces, matrix, fcmText;

    private List<Address> addresses = null;
    private Address address = null;

    private OSMPlaces currentPlace = null;

    private PlacesViewModel placesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //création d'une chaine de notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        //récupération de l'instance du viewmodel qu'on souhaite partager

        SingletonPlacesViewModelFactory singletonPlacesViewModelFactory =
                new SingletonPlacesViewModelFactory(PlacesViewModel.getInstance());


        placesViewModel = ViewModelProviders.of(this,singletonPlacesViewModelFactory).get(PlacesViewModel.class);

        name_place_edit = findViewById(R.id.name_place_edit);
        save_place_btn = findViewById(R.id.btn_save_place);
        details = findViewById(R.id.details_places_text);
        osmPlaces = findViewById(R.id.osm_places_text);
        matrix = findViewById(R.id.matrix_element_text);

        btnFcmToken = findViewById(R.id.btn_get_fcm_token);
        fcmText = findViewById(R.id.fcm_token_text);

        save_place_btn.setOnClickListener(this);
        btnFcmToken.setOnClickListener(this);

      //  getMatrixInfo();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    public String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", "empty");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    private void retrieveFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()){
                            Log.w(TAG, "getInstanceId failed", task.getException() );
                            return;
                        }

                        //récupération du token
                        String token = task.getResult().getToken();

                        String msg = getString(R.string.msg_token_fmt, token);


                        Log.d(TAG, "onComplete: "+msg);

                        fcmText.setText(msg);

                    }
                });
    }

    private void getAddress(){
      final   Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                //on décode l'adresse

                                    mLastLocation = location;

                                    try {

                                        if (location != null){
                                            addresses = geocoder.getFromLocation(
                                                    location.getLatitude(),
                                                    location.getLongitude(),
                                                    // In this sample, we get just a single address.
                                                    1);
                                        }
                                        if (addresses != null && addresses.size() != 0) address = addresses.get(0);

                                    } catch (IOException ioException) {
                                        // Catch network or other I/O problems.
                                        Log.e(TAG, "Network not available", ioException);
                                    } catch (IllegalArgumentException illegalArgumentException) {
                                        // Catch invalid latitude or longitude values.

                                        Log.e(TAG, "Invalid latlng" + ". " +
                                                "Latitude = " + location.getLatitude() +
                                                ", Longitude = " + location.getLongitude(), illegalArgumentException);
                                    }

                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG,"getLastLocation:onFailure", e);
                                }
                            });


    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {

                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {
                              showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {

        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void getOSMPlaces(String q, String countryCode){
        PlacesServices places = RetrofitClientInstance
                                            .getRetrofitOSMInstance()
                                            .create(PlacesServices.class);
        q = q.replace(" ","+").toLowerCase();
      final   String url = "?addressdetails=1&q="+q+"&format=json&countrycodes="+countryCode+"&limit=1";
        Call<List<OSMPlaces>> call = places.getOSMPlacesDetails(url);

        call.enqueue(new Callback<List<OSMPlaces>>() {
            @Override
            public void onResponse(Call<List<OSMPlaces>> call, Response<List<OSMPlaces>> response) {
                if (response.code() == 200 && !response.body().isEmpty() ){
                    OSMPlaces osmplace = response.body().get(0);
                    placesViewModel.setPlaceName("Nom: "+osmplace.getDisplay_name()+
                            "\nLatitude: "+osmplace.getLat()+
                            "\nLongitude "+osmplace.getLon() );


                    currentPlace = osmplace;

                }else if (response.code() != 404){
                    osmPlaces.setText(getString(R.string.no_place_found)+ "\n"+response.raw().request().url().toString());

                    showToast("Failed: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<List<OSMPlaces>> call, Throwable t) {
                Log.d(TAG,t.getMessage());
                showToast("Check internet connection");
            }
        });
    }

    private void sendPlacesToServer(cityPlaces places){
        PlacesServices placesServices = RetrofitClientInstance.getRetrofitPlacesInstance().create(PlacesServices.class);

        Call<cityPlaces> call = placesServices.postNewPlace(places);

        call.enqueue(new Callback<cityPlaces>() {
            @Override
            public void onResponse(Call<cityPlaces> call, Response<cityPlaces> response) {
                if (response.code() == 200){
                    showToast(""+ response.body().getCountryCode());
                }else showToast(""+response.code());
            }

            @Override
            public void onFailure(Call<cityPlaces> call, Throwable t) {
                Log.d(TAG,"sendPlacesOnFailure "+ t.getMessage());
                showToast("Check internet connection");
            }
        });

    }


    /**
     * Permet d'obténir la distance entre 2 points et le temps estimatif qu'il faut pour faire cette distance en voiture
     *
     * plustard remplacer les coordonnées fictives par de vraies
    * */
    private void getMatrixInfo(double lat1, double lon1, String lat2, String lon2){

        String body = "{  \"from_points\": [    [      -0.11379003524780275,   51.53664617804063    ]  ],  \"to_points\": [    [      -0.09576559066772462,   51.512882367963456    ]  ],  \"from_point_hints\": [   \"Copenhagen Street\"  ],  \"to_point_hints\": [   \"Cannon\" ],  \"out_arrays\": [    \"times\", \"distances\" ], \"vehicle\": \"car\"}";

      //  String url = "matrix?from_point=51.53664617804063,-0.11379003524780275&to_point=51.512882367963456,-0.09576559066772462&type=json&vehicle=car&debug=true&out_array=times&out_array=distances&key=d289c710-3054-40aa-8ee5-851d605f1fa5";

        String url = "matrix?from_point="+lat1+","+ lon1+"&to_point="+lat2+","+lon2+"&type=json&vehicle=car&debug=true&out_array=times&out_array=distances&key=d289c710-3054-40aa-8ee5-851d605f1fa5";

        final PlacesServices placesServices = RetrofitClientInstance.getRetrofitMatrixInstance(body).create(PlacesServices.class);

        Call<MatrixResponse> call = placesServices.getMatrixInformations(url);

        call.enqueue(new Callback<MatrixResponse>() {
            @Override
            public void onResponse(Call<MatrixResponse> call, Response<MatrixResponse> response) {
                if (response.code() == 200){
                    placesViewModel.setDistance(""+response.body().getDistances().get(0).get(0));

                  matrix.setText("Distance: "+response.body().getDistances().get(0).get(0) + " Temps: "+response.body().getTimes().get(0).get(0));
                }else {
//                    matrix.setText(response.raw().request().url().toString() + " "+call.request().body().contentType());
                    showToast("Matrix code response "+response.code());
                }
            }

            @Override
            public void onFailure(Call<MatrixResponse> call, Throwable t) {
                Log.d(TAG,"getMatrixOnFailure "+ t.getMessage());
                showToast("Check internet connection");
            }
        });

    }



    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btn_save_place){
            getAddress();

            if (TextUtils.isEmpty(name_place_edit.getText())){
                showToast(getString(R.string.hint_entrer_un_lieu));
            }else {
                //teste si l'adress n'est pas vide
            final String placeEntered = name_place_edit.getText().toString();
                if (address == null && mLastLocation != null ){
                    details.setText("Nom de la place: "+placeEntered+
                            "\nLatitude: "+ mLastLocation.getLatitude() + ", Longitude: "+mLastLocation.getLongitude());
                }else if (mLastLocation != null){

                    sendPlacesToServer(new cityPlaces(name_place_edit.getText().toString(),
                                                        mLastLocation.getLongitude(),
                                                        mLastLocation.getLatitude(),
                                                        address.getCountryCode(),
                                                        address.getLocality(),
                                                        address.getCountryName()));

                    placesViewModel.setPlaceName(placeEntered);

                    getOSMPlaces(placeEntered+ "+"+ address.getLocality().toLowerCase() , address.getCountryCode().toLowerCase());

                    placesViewModel.getPlaceName().observe(MainActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            osmPlaces.setText( s );
                        }
                    });

                    if (currentPlace != null){


                        getMatrixInfo(mLastLocation.getLatitude(), mLastLocation.getLongitude(), currentPlace.getLat(), currentPlace.getLon());
                    }else showToast("is null");

                    details.setText("Lieu entré: "+placeEntered+
                            "\nLatitude: "+ mLastLocation.getLatitude() + " Longitude: "+mLastLocation.getLongitude()+
                            "\nPays: "+address.getCountryName()+", Code pays: "+ address.getCountryCode() +  ", Ville: "+address.getLocality());


                }
            }
        }else if (id == R.id.btn_get_fcm_token){
           retrieveFCMToken();

           startActivity(new Intent(this, SecondActivity.class));

        }

    }


}
