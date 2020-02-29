package com.example.atrialfibrilationdetection.Feature.Pasien;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atrialfibrilationdetection.Feature.ProfileActivity;
import com.example.atrialfibrilationdetection.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PasienActivity extends Fragment {

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View  dialogView;
    private EditText et_device_id;
    private String device_id;

    private Calendar calendar;
    private String date;
    private SimpleDateFormat dateFormat;

    private FirebaseAuth mAuth;
    private DatabaseReference userRefs,riwayatRefs;
    private TextView tv_addordelete_device,tv_tgl_heartbeat,tv_device_id,tv_device_kosong,tv_separator,tv_riwayat_kosong;
    private CircleImageView circleImageView;

    private List<RiwayatModel> mLists = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.pasien_activity,container,false);
        return view;
    }

    private void initialize(){

        tv_addordelete_device = (TextView) getActivity().findViewById(R.id.tv_device);
        tv_device_id = getActivity().findViewById(R.id.tv_device_id);
        tv_tgl_heartbeat = getActivity().findViewById(R.id.tv_tgl_heartbeat);
        tv_device_kosong = getActivity().findViewById(R.id.tv_device_kosong);
        tv_riwayat_kosong = getActivity().findViewById(R.id.riwayat_kosong);
        tv_separator = getActivity().findViewById(R.id.tv_separator);
        mChart = getActivity().findViewById(R.id.lineChart);
        mRecyclerView = getActivity().findViewById(R.id.rvListDevice);
        circleImageView = (CircleImageView) getActivity().findViewById(R.id.civ_profile_image);

        mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getCurrentUser().getUid();
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUID);
        riwayatRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUID).child("riwayat");

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());

        checkDate(date);

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String following = dataSnapshot.child("subscription").getValue().toString();
                if (following.equals("-")) {
                    tv_addordelete_device.setText("Tambah Device");
                    tv_device_id.setVisibility(View.INVISIBLE);
                    tv_device_kosong.setVisibility(View.VISIBLE);
                    tv_tgl_heartbeat.setVisibility(View.INVISIBLE);
                    tv_separator.setVisibility(View.INVISIBLE);
                    mChart.setVisibility(View.GONE);
                    tv_addordelete_device.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initializeDialog();
                        }
                    });
                } else {
                    tv_addordelete_device.setText("Hapus Device");
                    tv_device_id.setText(following);
                    tv_tgl_heartbeat.setText(date);
                    tv_device_id.setVisibility(View.VISIBLE);
                    tv_separator.setVisibility(View.VISIBLE);
                    tv_device_kosong.setVisibility(View.INVISIBLE);
                    tv_tgl_heartbeat.setVisibility(View.VISIBLE);
                    mChart.setVisibility(View.VISIBLE);
                    tv_addordelete_device.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDevice();
                        }
                    });
                }
                initializeChart(following);
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        riwayatRefs.limitToLast(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tv_riwayat_kosong.setVisibility(View.GONE);
                    mLists.clear();
                    for (DataSnapshot dsp:dataSnapshot.getChildren()){
                        for (DataSnapshot dats: dsp.getChildren()){
                            mLists.add(new RiwayatModel(dats.child("device_id").getValue().toString(),dsp.getKey()));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    mRecyclerView.setVisibility(View.GONE);
                    tv_riwayat_kosong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivityInLeft(ProfileActivity.class);
            }
        });

    }

    private void checkDate(final String date){

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String following = dataSnapshot.child("subscription").getValue().toString();
                riwayatRefs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.getKey().equals(date)){
                            riwayatRefs.child(date).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.getKey().equals(following) && !following.equals("-")){
                                        HashMap userMap = new HashMap();
                                        userMap.put(following+"/af",0);
                                        userMap.put(following+"/normal",0);
                                        userMap.put(following+"/average_af",0);
                                        userMap.put(following+"/device_id",following);
                                        riwayatRefs.child(date).updateChildren(userMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteDevice(){
        dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Anda yakin ingin menghapus device ini?");
        dialog.setCancelable(false);
        dialog.setTitle(Html.fromHtml("<font color='#416188'>Delete Device</font>"));

        dialog.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                HashMap userMap = new HashMap();
                userMap.put("subscription","-");
                userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Hapus Berhasil", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    }
                });
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initRecyclerView() {

        mRecyclerView = getActivity().findViewById(R.id.rvListDevice);
        mAdapter = new RiwayatAdapter(mLists,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initializeDialog(){
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_device,null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.ic_wristwatch);
        dialog.setTitle(Html.fromHtml("<font color='#416188'>Tambah Device</font>"));


        et_device_id = dialogView.findViewById(R.id.et_device_id);

        dialog.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                device_id = et_device_id.getText().toString();
                if(device_id.isEmpty()){
                    Toast.makeText(getActivity(), "Silahkan isi Device ID terlebih dahulu", Toast.LENGTH_SHORT).show();
                }else{
                    calendar = Calendar.getInstance();
                    dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    date = dateFormat.format(calendar.getTime());

                    HashMap userMap = new HashMap();
                    userMap.put("subscription", device_id);
                    userMap.put("riwayat/"+date+"/"+device_id+"/af",0);
                    userMap.put("riwayat/"+date+"/"+device_id+"/normal",0);
                    userMap.put("riwayat/"+date+"/"+device_id+"/average_af",0);
                    userMap.put("riwayat/"+date+"/"+device_id+"/device_id",device_id);
                    userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Tambahkan Berhasil", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initializeChart(final String topics){

        // disable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.setScaleMinima(10f, 1f);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(-50f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(getActivity(), "tcp://192.168.1.6:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected

                    Log.d("1234567890",topics);
                    Toast.makeText(getActivity().getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();

                    String topic = topics;
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Toast.makeText(getActivity(), "Subscribe Success!", Toast.LENGTH_SHORT).show();
                                client.setCallback(new MqttCallback() {
                                    @Override
                                    public void connectionLost(Throwable cause) {

                                    }

                                    @Override
                                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                                        addEntry(Float.parseFloat(new String(message.getPayload())));
                                    }

                                    @Override
                                    public void deliveryComplete(IMqttDeliveryToken token) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(getActivity(), "Subscribe Failure!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getActivity(), "Not Conncected!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void addEntry(float event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), event), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private void setActivityInLeft(Class activity) {
        Intent mainIntent = new Intent(getActivity(), activity);
        startActivity(mainIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}

