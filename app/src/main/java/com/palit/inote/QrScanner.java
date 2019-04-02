package com.palit.inote;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class QrScanner extends AppCompatActivity {

    private EditText ed;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    ImageView imageView;
    AlertDialog ad;
    static final int REQUEST_CAMERA = 10;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mProfileDatabaseReference = database.getReference();

    ByteArrayOutputStream bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        imageView = findViewById(R.id.image);



        AlertDialog.Builder builder = new AlertDialog.Builder(QrScanner.this);
        builder.setTitle("Notebook Detail");
        final View view1 = getLayoutInflater().inflate(R.layout.dialog_book_name,null);
        builder.setView(view1);
        final Button b = view1.findViewById(R.id.submit);


        builder.setCancelable(true);
        ad = builder.create();
        ad.show();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed = view1.findViewById(R.id.notebook_name);
                Toast.makeText(QrScanner.this, "Science", Toast.LENGTH_SHORT).show();
                ad.dismiss();
               // dispatchTakePictureIntent();
                cameraIntent();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
           /* if(data!=null) {
                Bundle extras = data.getExtras();*/

           try {
               Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(currentPhotoPath));
               imageView.setImageBitmap(imageBitmap);
           }catch (Exception e){
               e.printStackTrace();
           }
            }
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            onCaptureImageResult(data);
        }

    }

    private void dispatchTakePictureIntent() {
        Toast.makeText(this, "Inside take Picture Intent", Toast.LENGTH_SHORT).show();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "File creation error", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.palit.inote.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        else
            Toast.makeText(this, "get Package manager", Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        Log.d("TimeStamp",timeStamp);
        String imageFileName = ed.getText().toString()+"JPEG_" + timeStamp + "_";
        Log.d("FileName",imageFileName);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("Storage dire",storageDir.getPath());
        File image = null;
        try {
             image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }catch (Exception e){
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(thumbnail);
        uploadPhoto();
    }

    private void uploadPhoto(){
        String noteName = ed.getText().toString().trim();
        String path = "Notebooks/"+user.getUid()+"/"+noteName+"/"+noteName+ UUID.randomUUID()+".png";
        StorageReference notePageRef = storage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("PageNo.","1")
                .build();
        UploadTask uploadTask = notePageRef.putBytes(bytes.toByteArray(),metadata);
        uploadTask.addOnSuccessListener(QrScanner.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(QrScanner.this, "Upload Sucess", Toast.LENGTH_SHORT).show();
                addNoteBook();
            }
        });
        uploadTask.addOnFailureListener(QrScanner.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void addNoteBook(){
        String key = mProfileDatabaseReference.child("Users").child(user.getUid()).getKey();
        mProfileDatabaseReference.child("Users").child(user.getUid()).child(key).child("mNoteBooks").setValue(ed.getText().toString());
    }
}
