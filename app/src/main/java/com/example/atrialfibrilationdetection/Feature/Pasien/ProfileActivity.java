package com.example.atrialfibrilationdetection.Feature.Pasien;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.atrialfibrilationdetection.Feature.Auth.LoginActivity;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private PasienActivity pasienActivity;

    private Button btn_logout, btn_ipserver;
    private TextView tv_recent_ipserver,tv_selesai;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;

    private AlertDialog.Builder dialog;private LayoutInflater inflater;
    private View  dialogView;
    private EditText et_newserver;
    private String ip_server;

    private DatabaseReference userRefs;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        pasienActivity = new PasienActivity();

        btn_logout = findViewById(R.id.btn_logout);
        btn_ipserver = findViewById(R.id.btn_ip_server);
        tv_selesai = findViewById(R.id.tv_profile_selesai);
        circleImageView = findViewById(R.id.civ_profile_pp);
        progressBar = findViewById(R.id.progressbar_pp);

        mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getCurrentUser().getUid();
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUID);

        btn_ipserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDialogIP();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertsignout();
            }
        });

        tv_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void alertsignout(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog2.setTitle("Konfirmasi Keluar");

        // Setting Dialog Message
        alertDialog2.setMessage("Apakah anda yakin ingin keluar?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                FirebaseAuth.getInstance().signOut();
                closeActivity(LoginActivity.class);
            }
        });

        alertDialog2.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    private void initializeDialogIP(){
        dialog = new AlertDialog.Builder(this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.ipserver,null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(Html.fromHtml("<font color='#416188'>IP Server</font>"));

        et_newserver = dialogView.findViewById(R.id.et_new_server);
        tv_recent_ipserver = dialogView.findViewById(R.id.tv_server);

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_recent_ipserver.setText(dataSnapshot.child("ip").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                ip_server = et_newserver.getText().toString();
                if(ip_server.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Silahkan isi IP Server terlebih dahulu", Toast.LENGTH_SHORT).show();
                    initializeDialogIP();
                }else{
                    HashMap userMap = new HashMap();
                    userMap.put("ip", ip_server);
                    userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Ubah Berhasil", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(ProfileActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void closeActivity(Class activity) {
        Intent mainIntent = new Intent(ProfileActivity.this, activity);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

}
