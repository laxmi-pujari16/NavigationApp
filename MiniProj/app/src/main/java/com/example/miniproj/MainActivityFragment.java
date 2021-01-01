package com.example.miniproj;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View mView;
    private Button mZoomButton;
    private Button mLocationButton;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_main, container, false);
        mZoomButton = mView.findViewById(R.id.zoom_view_button);
        mZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                getActivity().startActivity(i);
            }
        });
        return mView;
    }
}
