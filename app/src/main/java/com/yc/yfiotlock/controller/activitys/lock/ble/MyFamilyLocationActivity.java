package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.FamilyInfo;
import com.yc.yfiotlock.utils.MapUtils;
import com.yc.yfiotlock.view.adapters.LocationAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyFamilyLocationActivity extends BaseActivity implements BaiduMap.OnMapStatusChangeListener, OnGetGeoCoderResultListener {

    private LocationAdapter locationAdapter;

    public static void start(Context context, FamilyInfo familyInfo) {
        Intent intent = new Intent(context, MyFamilyLocationActivity.class);
        if (familyInfo != null) {
            intent.putExtra("family_info", familyInfo);
        }
        context.startActivity(intent);
    }

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.mapview_location)
    MapView mMapView;
    @BindView(R.id.poi_list_location)
    RecyclerView mRecyclerView;

    private BaiduMap mBaiduMap;
    private LatLng mCenter;
    // 默认逆地理编码半径范围
    private static final int sDefaultRGCRadius = 500;
    private GeoCoder mGeoCoder = null;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_family_location;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        initRecyclerView();
        initMap();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        locationAdapter = new LocationAdapter(null);
        mRecyclerView.setAdapter(locationAdapter);
    }


    private void initMap() {
        mBaiduMap = mMapView.getMap();
        if (null == mBaiduMap) {
            return;
        }

        // 设置初始中心点为北京
        mCenter = new LatLng(39.963175, 116.400244);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mCenter, 16);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBaiduMap.setOnMapStatusChangeListener(this);
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                createCenterMarker();
                reverseRequest(mCenter);
            }
        });
    }

    /**
     * 创建地图中心点marker
     */
    private void createCenterMarker() {
        Projection projection = mBaiduMap.getProjection();
        if (null == projection) {
            return;
        }

        Point point = projection.toScreenLocation(mCenter);
        BitmapDescriptor bitmapDescriptor =
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_binding_point);
        if (null == bitmapDescriptor) {
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(mCenter)
                .icon(bitmapDescriptor)
                .flat(false)
                .fixedScreenPosition(point);
        mBaiduMap.addOverlay(markerOptions);
        bitmapDescriptor.recycle();
    }

    /**
     * 逆地理编码请求
     *
     * @param latLng
     */
    private void reverseRequest(LatLng latLng) {
        if (null == latLng) {
            return;
        }

        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption().location(latLng)
                .newVersion(1) // 建议请求新版数据
                .radius(sDefaultRGCRadius);

        if (null == mGeoCoder) {
            mGeoCoder = GeoCoder.newInstance();
        }

        mGeoCoder.setOnGetGeoCodeResultListener(this);
        mGeoCoder.reverseGeoCode(reverseGeoCodeOption);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
    }

    private boolean mStatusChangeByItemClick = false;

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        LatLng newCenter = mapStatus.target;
        // 如果是点击poi item导致的地图状态更新，则不用做后面的逆地理请求，
        if (mStatusChangeByItemClick) {
            if (!MapUtils.isLatlngEqual(mCenter, newCenter)) {
                mCenter = newCenter;
            }
            mStatusChangeByItemClick = false;
            return;
        }

        if (!MapUtils.isLatlngEqual(mCenter, newCenter)) {
            mCenter = newCenter;
            reverseRequest(mCenter);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (null == reverseGeoCodeResult) {
            return;
        }

        VUiKit.post(new Runnable() {
            @Override
            public void run() {
                updateUI(reverseGeoCodeResult);
            }
        });
    }

    /**
     * 更新UI
     *
     * @param reverseGeoCodeResult
     */
    private void updateUI(ReverseGeoCodeResult reverseGeoCodeResult) {
        List<PoiInfo> poiInfos = reverseGeoCodeResult.getPoiList();

        PoiInfo curAddressPoiInfo = new PoiInfo();
        curAddressPoiInfo.address = reverseGeoCodeResult.getAddress();
        curAddressPoiInfo.location = reverseGeoCodeResult.getLocation();

        if (null == poiInfos) {
            poiInfos = new ArrayList<>(2);
        }
        if (null != locationAdapter) {
            locationAdapter.setNewInstance(poiInfos);
        }
    }
}
