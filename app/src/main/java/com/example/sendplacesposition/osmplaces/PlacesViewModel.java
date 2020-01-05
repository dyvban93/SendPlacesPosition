package com.example.sendplacesposition.osmplaces;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlacesViewModel extends ViewModel {
    private static PlacesViewModel placesViewModel;
    //nom de la place saisit
    private MutableLiveData<String> placeName = new MutableLiveData<>();;
    //distance entre ma position courrante et le lieu saisi
    private MutableLiveData<String> distance = new MutableLiveData<>();

    public static synchronized PlacesViewModel getInstance(){
        if (placesViewModel == null){
            placesViewModel = new PlacesViewModel();

        }

        return placesViewModel;
    }


    public MutableLiveData<String> getPlaceName(){

        return placeName;
    }

    public MutableLiveData<String> getDistance(){

        return distance;
    }

    public void setPlaceName(String placeName){
       this.placeName.setValue(placeName);
    }

    public void setDistance(String distance){
        this.distance.setValue(distance);
    }
}
