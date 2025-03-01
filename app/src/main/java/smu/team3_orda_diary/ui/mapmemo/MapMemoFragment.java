package smu.team3_orda_diary.ui.mapmemo;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.database.DBHelper;
import smu.team3_orda_diary.databinding.DialogEditMapBinding;
import smu.team3_orda_diary.databinding.FragmentMapMemoBinding;
import smu.team3_orda_diary.model.MapMemoItem;

public class MapMemoFragment extends Fragment implements OnMapReadyCallback {
    private DBHelper dbHelper;
    private FragmentMapMemoBinding binding;
    private GoogleMap mMap;
    private Geocoder geocoder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBHelper.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapMemoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        binding.mapBtnSearch.setOnClickListener(v -> searchLocation());
        binding.mapBtnMark.setOnClickListener(v -> showAddMarkerDialog());
    }

    private void searchLocation() {
        String searchResult = binding.mapEtPlace.getText().toString();
        List<Address> addressList;

        try {
            geocoder = new Geocoder(requireContext());
            addressList = geocoder.getFromLocationName(searchResult, 10);

            if (addressList == null || addressList.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.map_no_results), Toast.LENGTH_SHORT).show();
                return;
            }

            Address address = addressList.get(0);
            LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), getString(R.string.map_address_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddMarkerDialog() {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Material_Light_Dialog);
        DialogEditMapBinding dialogBinding = DialogEditMapBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.mapDialogBtnOk.setOnClickListener(v -> {
            String title = dialogBinding.mapDialogTitle.getText().toString();
            String content = dialogBinding.mapDialogContent.getText().toString();

            if (mMap != null) {
                LatLng currentPosition = mMap.getCameraPosition().target;
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(title)
                        .snippet(content)
                        .position(currentPosition);

                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
                Toast.makeText(requireContext(), getString(R.string.map_marker_added), Toast.LENGTH_LONG).show();

                dbHelper.insertMapMemo(title, content, currentPosition.latitude, currentPosition.longitude);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng SEOUL = new LatLng(37.56, 126.97);
        mMap.addMarker(new MarkerOptions().position(SEOUL).title("서울").snippet("한국의 수도"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));

        loadSavedMarkers();
    }

    private void loadSavedMarkers() {
        ArrayList<MapMemoItem> mapMemoList = dbHelper.getAllMapMemos();

        for (MapMemoItem memo : mapMemoList) {
            LatLng location = new LatLng(memo.getLatitude(), memo.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(memo.getTitle())
                    .snippet(memo.getContent())
                    .position(location);

            mMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
