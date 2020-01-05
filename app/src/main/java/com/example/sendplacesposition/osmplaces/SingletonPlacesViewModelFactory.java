package com.example.sendplacesposition.osmplaces;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * ce viewmodel sera partagé dans toute l'application
 * Grace à lui, on pourra avoir accès aux données des viewModel dans toute l'appli
 * */
public class SingletonPlacesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private PlacesViewModel mPlacesViewModel;
    private final Map<Class<? extends ViewModel>, ViewModel> mFactory = new HashMap<>();

    public SingletonPlacesViewModelFactory(PlacesViewModel  placesViewModel){
        mPlacesViewModel = placesViewModel;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(final @NonNull Class<T> modelClass) {
        mFactory.put(modelClass, mPlacesViewModel);

        if (PlacesViewModel.class.isAssignableFrom(modelClass)) {
            PlacesViewModel shareVM = null;

            if (mFactory.containsKey(modelClass)) {
                shareVM = (PlacesViewModel) mFactory.get(modelClass);
            } else {
                try {
                    shareVM = (PlacesViewModel) modelClass.getConstructor(Runnable.class).newInstance(new Runnable() {
                        @Override
                        public void run() {
                            mFactory.remove(modelClass);
                        }
                    });
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                }
                mFactory.put(modelClass, shareVM);
            }

            return (T) shareVM;
        }
        return super.create(modelClass);
    }

}
