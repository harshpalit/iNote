package com.palit.inote;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LinearLayout linearLayout;
    private TextView mName,mScanText,mGreet;

    private FirebaseDatabase mDataBase;
    private DatabaseReference mDataBaseReference;
    private UserDetails userDetails;

    private String username,email,phone;
    private Button mOcr;

    private int RC_PHOTO_PICKER = 4;
    private FirebaseVisionImage image;
    private FirebaseVisionTextRecognizer detector;
    private ProgressBar mProgressScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Get FireBase instance and get the user details
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        transparentToolbar();
        setContentView(R.layout.activity_home_screen);

        //Initiate the views
        mName = findViewById(R.id.name);
        mScanText = findViewById(R.id.scanned_text);
        mOcr = findViewById(R.id.ocr_scan);
        mProgressScan = findViewById(R.id.progress_scan);
        linearLayout = findViewById(R.id.parent);
        mGreet = findViewById(R.id.welcome_greet);

        Date currentTime = Calendar.getInstance().getTime();
        int hour = currentTime.getHours();
        if(hour>6&&hour<12){
            mGreet.setText("Good Morning");
        }
        else if(hour>12&&hour<16){
            mGreet.setText("Good Afternoon");
        }else mGreet.setText("Good Evening");

        //Set uup FireBase Text Detector
        detector = FirebaseVision.getInstance()
                .getCloudTextRecognizer();

        mOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start intent for picking up the Image
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        //Get FireBase DataBase Reference
        mDataBase = FirebaseDatabase.getInstance();
        mDataBaseReference = mDataBase.getReference().child("Users").child(mUser.getUid());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scanner();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        final TextView tUsername = v.findViewById(R.id.username);
        final TextView tEmail = v.findViewById(R.id.textView);


        mDataBaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datsnapshot",dataSnapshot.toString());
                userDetails = dataSnapshot.getValue(UserDetails.class);
                tUsername.setText(userDetails.getmName());
                mName.setText(userDetails.getmName());
                tEmail.setText(userDetails.getmEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       /* mDataBaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("datsnapshot",dataSnapshot.toString());
                userDetails = dataSnapshot.getValue(UserDetails.class);
                username = userDetails.getmName();
                email = userDetails.getmEmail();
                mName.setText(username);
                tUsername.setText(username);
                tEmail.setText(email);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            startActivity(new Intent(this,MyNotebooks.class));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

            Scanner();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out) {
            mAuth.signOut();
            startActivity(new Intent(getBaseContext(),LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void transparentToolbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents().equals("https://play.google.com/store/apps/details?id=com.palit.harsh.srmgpacalculator")) {
                    startActivity(new Intent(getBaseContext(),QrScanner.class));
                }
                else {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "Scan QrCode on back of the iNote", Snackbar.LENGTH_LONG)
                            .setAction("Scan Again", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Scanner();
                                }
                            });

                    snackbar.show();
                }

            }
        }else if(requestCode == RC_PHOTO_PICKER && resultCode==RESULT_OK){

            //FireBase Scan Image Process
            mProgressScan.setVisibility(View.VISIBLE);

            //get the Uri of the file
            Uri imageUri = data.getData();
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageUri);

            }catch (IOException e){
                e.printStackTrace();
            }

            Task<FirebaseVisionText> result1 =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    String resultText = firebaseVisionText.getText();
                                    mScanText.setText(resultText);
                                    mProgressScan.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            mProgressScan.setVisibility(View.GONE);
                                            Toast.makeText(HomeScreen.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Scanner method for running the QrScanner
    private void Scanner(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a barcode on the NoteBook");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

}