package smu.team3_orda_diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapMemoActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private EditText et_place;
    private Button btn_search, btn_add_mark;
    private Geocoder geocoder;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_memo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //버튼, 검색창 객체 생성
        et_place = (EditText) findViewById(R.id.map_et_place);
        btn_search = (Button) findViewById(R.id.map_btn_search);
        btn_add_mark = (Button) findViewById(R.id.map_btn_mark);



    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {


        mMap = googleMap;
        geocoder = new Geocoder(this);
        // 버튼 터치 이벤트 구현
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_result = et_place.getText().toString();
                // 주소값 초기 세팅
                List<Address>addressList = null;
                try {
                    //editText에 입력한 텍스트(주소, 지역, 장소 등) 을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            search_result, // 주소
                            10); // 최대 검색 결과 개수

                }
                catch (IOException e){
                    e.printStackTrace();

                }
                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));

                btn_add_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // dialog에서 텍스트 받아서 마커 추가할 것임
                        //팝업창 띄우기
                        Dialog dialog = new Dialog(MapMemoActivity.this, android.R.style.Theme_Material_Light_Dialog);
                        dialog.setContentView(R.layout.dialog_edit_map);
                        EditText et_title = dialog.findViewById(R.id.map_dialog_title);
                        EditText et_content = dialog.findViewById(R.id.map_dialog_content);
                        Button btn_ok = dialog.findViewById(R.id.map_dialog_btn_ok);
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //update database
                                String title = et_title.getText().toString();
                                String content = et_content.getText().toString();
                                dialog.dismiss();
                                Toast.makeText(MapMemoActivity.this, "마커가 추가되었습니다.", Toast.LENGTH_LONG).show();
                                // 마커 생성
                                MarkerOptions mOptions2 = new MarkerOptions();
                                mOptions2.title(title);
                                // 주소 띄우고 싶으면 adrress 를 그냥 넣으면 됨
                                mOptions2.snippet(content);
                                mOptions2.position(point);
                                // 마커 추가
                                mMap.addMarker(mOptions2);
                                // 해당 좌표로 화면 줌
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));

                            }
                        });
                        dialog.show();


                    }
                });


            }
        });
        // 맵 터치 이벤트 구현
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                //마커 타이틀
                mOptions.title("마커 좌표");
                Double latitude = point.latitude; //위도
                Double longitude = point.longitude; //경도
                //마커의 스티펫(간단한 텍스트) 설정
                mOptions.snippet(latitude.toString()+","+longitude.toString());
                //Lating : 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                //마커(핀) 추가
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));

                //googleMap.addMarker(mOptions);

            }
        });


        LatLng SEOUL = new LatLng(37.56, 126.97);
        MarkerOptions markerOptions = new MarkerOptions(); //marker 옵션 객체 생성
        markerOptions.position(SEOUL); // 마커에다가 서울 위치값 넣음
        markerOptions.title("서울"); // 마커 제목 설정
        markerOptions.snippet("한국의 수도"); // 마커 내용 설정
        mMap.addMarker(markerOptions); // 구글맵에다가 마커 추가
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10)); // 카메라 위치를 서울로 옮김

    }
}