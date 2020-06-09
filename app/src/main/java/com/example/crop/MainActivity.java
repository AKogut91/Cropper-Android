package com.example.crop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent;
    Button cameraButton;
    Button pictureButton;

    final int RequestPerissionCode = 1;
    final int CAMERA_CAPTURE = 1;
    final int CODE_GALLERY = 3;
    final int CODE_CROP = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestPermissionCode();
        }
        setUI();
        setActions();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE) {
            uri = data.getData();
            cropImage();

        } else if (requestCode == CODE_GALLERY) {
            if(data != null) {
                uri = data.getData();
                cropImage();
            }

        } else if (requestCode == CODE_CROP) {
            if(data != null) {
                uri = data.getData();
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                imageView.setImageBitmap(bitmap);
                Toast toast = Toast.makeText(this, "file from crop gallery", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    // Permission

    private void requestPermissionCode() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast toast = Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, RequestPerissionCode);
        }
    }

    // UI

    private void setUI() {
        cameraButton = findViewById(R.id.camera);
        pictureButton = findViewById(R.id.picture);
        imageView = findViewById(R.id.imageView);
    }

    private void setActions() {

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CamIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(CamIntent, CAMERA_CAPTURE);
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), CODE_GALLERY);
            }
        });
    }

    // Crop

    private void cropImage() {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", true);
            CropIntent.putExtra("otputX", 200);
            CropIntent.putExtra("otputY", 200);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 3);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, CODE_CROP);

        } catch (ActivityNotFoundException ex) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case  RequestPerissionCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast toast = Toast.makeText(this, "Granted", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }
}