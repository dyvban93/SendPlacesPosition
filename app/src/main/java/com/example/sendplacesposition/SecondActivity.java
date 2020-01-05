package com.example.sendplacesposition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.TextView;

import com.example.sendplacesposition.osmplaces.PlacesViewModel;
import com.example.sendplacesposition.osmplaces.SingletonPlacesViewModelFactory;

public class SecondActivity extends AppCompatActivity {
    private TextView place, distance;

    private PlacesViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        SingletonPlacesViewModelFactory singletonPlacesViewModelFactory =
                new SingletonPlacesViewModelFactory(PlacesViewModel.getInstance());

        model = ViewModelProviders.of(this,singletonPlacesViewModelFactory).get(PlacesViewModel.class);

        place = findViewById(R.id.osm_places_viewmodel_text);
        distance = findViewById(R.id.matrix_viewmodel_text);

        model.getPlaceName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                place.setText(s);
            }
        });

        model.getDistance().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                distance.setText(s);
            }
        });
    }
}
