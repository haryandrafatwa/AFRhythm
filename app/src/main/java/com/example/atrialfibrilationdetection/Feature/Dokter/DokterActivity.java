package com.example.atrialfibrilationdetection.Feature.Dokter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview.DeviceAdapter;
import com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview.DeviceModel;
import com.example.atrialfibrilationdetection.Feature.Dokter.Recyclerview.RumahSakit;
import com.example.atrialfibrilationdetection.Feature.Pasien.ProfileActivity;
import com.example.atrialfibrilationdetection.Feature.Pasien.RiwayatAdapter;
import com.example.atrialfibrilationdetection.Feature.Pasien.RiwayatModel;
import com.example.atrialfibrilationdetection.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DokterActivity extends Fragment {

    private String device_id, nama_pasien, rumah_sakit;
    private int pasienCount;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private CircleImageView circleImageView;
    private TextView tv_addordelete_device,tv_device_kosong;
    private EditText et_device_id, et_nama_pasien, et_rumahsakit;

    private FirebaseAuth mAuth;
    private DatabaseReference userRefs,dokterRefs;

    private ProgressBar progressBar;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View  dialogView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dokter_activity,container,false);
        return view;
    }

    private void initialize(){

        circleImageView = getActivity().findViewById(R.id.civ_profile_image_dokter);
        tv_addordelete_device = getActivity().findViewById(R.id.add_device_dokter);
        tv_device_kosong = getActivity().findViewById(R.id.device_kosong_dokter);
        progressBar = getActivity().findViewById(R.id.progressbar_menu);

        mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getCurrentUser().getUid();
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUID);
        dokterRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUID).child("perangkat");

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                tv_addordelete_device.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initializeDialogDevice();
                    }
                });
                if(dataSnapshot.hasChild("displaypicture")){
                    Picasso.get().load(dataSnapshot.child("displaypicture").getValue().toString()).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            circleImageView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }else{
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/af-detection.appspot.com/o/DisplayPictures%2Fdummy%2Fic_profile.png?alt=media&token=00d9cd5d-c2ed-4bfd-93a7-87765f3cb607").into(circleImageView);
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dokterRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_device_kosong.setVisibility(View.GONE);

                    final List<RumahSakit> rumahSakitList = new ArrayList<>();
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String rsKey = snapshot.getKey();

                        dokterRefs.child(rsKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final List<DeviceModel> deviceList = new ArrayList<>();
                                String rsName="";
                                for (DataSnapshot dsp: dataSnapshot.getChildren()){
                                    deviceList.add(new DeviceModel(dsp.child("device_id").getValue().toString(),dsp.child("nama_pasien").getValue().toString()));
                                    rsName = dsp.child("nama_rumahsakit").getValue().toString();
                                }
                                rumahSakitList.add(new RumahSakit(rsName,deviceList));
                                DeviceAdapter deviceAdapter = new DeviceAdapter(rumahSakitList);
                                recyclerView.setAdapter(deviceAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }else{
                    recyclerView.setVisibility(View.GONE);
                    tv_device_kosong.setVisibility(View.VISIBLE);
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

    private void initializeDialogDevice(){
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_device_dokter,null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(Html.fromHtml("<font color='#416188'>Tambah Device</font>"));


        et_device_id = dialogView.findViewById(R.id.et_device_id_dokter);
        et_nama_pasien = dialogView.findViewById(R.id.et_nama_pasien);
        et_rumahsakit = dialogView.findViewById(R.id.et_rumahsakit);

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pasienCount = Integer.valueOf(dataSnapshot.child("pasienCount").getValue().toString())+1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                device_id = et_device_id.getText().toString();
                nama_pasien = et_nama_pasien.getText().toString();
                rumah_sakit = et_rumahsakit.getText().toString();
                if(rumah_sakit.isEmpty()){
                    Toast.makeText(getActivity(), "Silahkan isi Nama Rumah Sakit terlebih dahulu", Toast.LENGTH_SHORT).show();
                    initializeDialogDevice();
                }else{
                    if(device_id.isEmpty()){
                        Toast.makeText(getActivity(), "Silahkan isi ID Perangkat terlebih dahulu", Toast.LENGTH_SHORT).show();
                        initializeDialogDevice();
                    }else{
                        if(nama_pasien.isEmpty()){
                            Toast.makeText(getActivity(), "Silahkan isi Nama Pasien terlebih dahulu", Toast.LENGTH_SHORT).show();
                            initializeDialogDevice();
                        }else{
                            try{
                                String[] dotSeparated = rumah_sakit.split("\\. ");
                                String newString = "";
                                for (int i = 0; i < dotSeparated.length; i++) {
                                    newString = newString + dotSeparated[i];
                                }
                                String[] spaceSeparated = newString.split("\\ ");
                                String validString = "";
                                for (int i = 0; i < spaceSeparated.length; i++) {
                                    validString = validString + spaceSeparated[i];
                                }

                                HashMap userMap = new HashMap();
                                userMap.put(validString.toLowerCase()+"/"+device_id+"/device_id",device_id);
                                userMap.put(validString.toLowerCase()+"/"+device_id+"/nama_rumahsakit", rumah_sakit);
                                userMap.put(validString.toLowerCase()+"/"+device_id+"/nama_pasien",nama_pasien);
                                dokterRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
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
                            }catch (NumberFormatException e){
                                Toast.makeText(getActivity(), "Error : " + e, Toast.LENGTH_SHORT).show();
                            }

                            userRefs.child("pasienCount").setValue(pasienCount);
                        }
                    }
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

    private void initRecyclerView() {

        recyclerView = getActivity().findViewById(R.id.rvListDevice);
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setActivityInLeft(Class activity) {
        Intent mainIntent = new Intent(getActivity(), activity);
        startActivity(mainIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
