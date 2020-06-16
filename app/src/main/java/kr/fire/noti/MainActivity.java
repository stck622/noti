package kr.fire.noti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener  {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    ArrayList<MapPOIItem> markers = new ArrayList<>();
    boolean first_load = true;
    MapView mMapView;
    int clickTag = 0;

    TextView gear_name;
    TextView gear_addr;
    TextView gear_gas_value;
    TextView gear_fire_value;
    Button gear_cctv_bt;

    fbData fbdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = new MapView(this);
        RelativeLayout mMapViewContainer = findViewById(R.id.map_mv_mapcontainer);
        mMapViewContainer.addView(mMapView);

        gear_addr = findViewById(R.id.tv_gear_addr);
        gear_name = findViewById(R.id.tv_gear_title);
        gear_gas_value = findViewById(R.id.tv_gear_gas_value);
        gear_fire_value = findViewById(R.id.tv_gear_fire_value);
        gear_cctv_bt = findViewById(R.id.bt_cctv);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbdata = dataSnapshot.getValue(fbData.class);
                if(first_load){
                    first_load = false;
                    for(int i = 0; i < fbdata.gear.values().toArray().length; i++){
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(((Map<String,String>)fbdata.gear.values().toArray()[i]).get("Name"));
                        marker.setTag(i);
                        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(((Map<String,String>)fbdata.gear.values().toArray()[i]).get("posX")),Double.parseDouble(((Map<String,String>)fbdata.gear.values().toArray()[i]).get("posY"))));
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                        mMapView.setPOIItemEventListener(MainActivity.this);
                        mMapView.addPOIItem(marker);
                        markers.add(marker);
                    }

                    mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(((Map<String,String>)fbdata.gear.values().toArray()[0]).get("posX")),Double.parseDouble(((Map<String,String>)fbdata.gear.values().toArray()[0]).get("posY"))), true);
                    mMapView.setZoomLevel(7, true);
                    mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Double.parseDouble(((Map<String,String>)fbdata.gear.values().toArray()[0]).get("posX")),Double.parseDouble(((Map<String,String>)fbdata.gear.values().toArray()[0]).get("posY"))), 9, true);
                    mMapView.zoomIn(true);
                    mMapView.zoomOut(true);


                }

                gear_name.setText(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("Name"));
                gear_addr.setText(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("Address"));
                gear_gas_value.setText("GAS : "+((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("gasValue"));
                gear_fire_value.setText("FIRE : "+((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("fireValue"));

                if(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("cctv") == null){
                    gear_cctv_bt.setVisibility(View.INVISIBLE);
                } else {
                    gear_cctv_bt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        gear_cctv_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("cctv") != null){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("cctv")));
                    // intent.setPackage("com.android.chrome");   // 브라우저가 여러개 인 경우 콕 찍어서 크롬을 지정할 경우
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        clickTag = mapPOIItem.getTag();

        gear_name.setText(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("Name"));
        gear_addr.setText(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("Address"));
        gear_gas_value.setText("GAS : "+((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("gasValue"));
        gear_fire_value.setText("FIRE : "+((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("fireValue"));

        if(((Map<String,String>)fbdata.gear.values().toArray()[clickTag]).get("cctv") == null){
            gear_cctv_bt.setVisibility(View.INVISIBLE);
        } else {
            gear_cctv_bt.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
