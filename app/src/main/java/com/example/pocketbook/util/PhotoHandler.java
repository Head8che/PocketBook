package com.example.pocketbook.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PhotoHandler {

    private int LAUNCH_CAMERA_CODE = 1408;
    private int LAUNCH_GALLERY_CODE = 1922;

    String currentPhotoPath;
    Bitmap galleryPhoto;
    Boolean showRemovePhoto = false;

    /**
     * Image Option dialog that allows the user to take, choose, or remove a photo
     */
    public void showImageSelectorDialog(Activity activity, StorageReference defaultPhoto,
                                         ImageView layoutProfilePicture) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.alert_dialog_book_photo, null);

        // access the photo option text fields
        TextView takePhotoOption = view.findViewById(R.id.takePhotoField);
        TextView choosePhotoOption = view.findViewById(R.id.choosePhotoField);
        TextView showRemovePhotoOption = view.findViewById(R.id.removePhotoField);

        if (showRemovePhoto) {  // only show the Remove Photo option if the user has a photo
            showRemovePhotoOption.setVisibility(View.VISIBLE);
        } else {
            showRemovePhotoOption.setVisibility(View.GONE);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(activity).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // if the user opts to take a photo, open the camera
        takePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            openCamera(activity);
        });

        // if the user opts to choose a photo, open their gallery
        choosePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            activity.startActivityForResult(Intent.createChooser(intent, "Select Image"),
                    LAUNCH_GALLERY_CODE);
        });

        // if the user opts to remove their photo, replace their image with the default image
        showRemovePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            GlideApp.with(Objects.requireNonNull(activity.getApplicationContext()))
                    .load(defaultPhoto)
                    .into(layoutProfilePicture);
            currentPhotoPath = "REMOVE";
            showRemovePhoto = false;  // don't show Remove Photo option since user has no photo
        });
    }

    /**
     * Allows the camera to be initiated upon request from the user
     */
    private void openCamera(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]
                    {Manifest.permission.CAMERA}, LAUNCH_CAMERA_CODE);
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                // catch errors that occur while creating the file
                Log.e("SIGN_UP_ACTIVITY", ex.toString());
            }
            // continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.fileprovider",
                        photoFile);
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // open the camera
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    activity.startActivityForResult(takePictureIntent, LAUNCH_CAMERA_CODE);
                }
            }
        } else {  // if there's no camera activity to handle the intent
            Log.e("SIGN_UP_ACTIVITY", "Failed to resolve activity!");
        }

    }

    /**
     * Create an image file for the images to be stored
     * @return the created image
     * @throws IOException exception if creating the image file fails
     */
    private File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.CANADA).format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Sets the user's photo to the image from either the camera or the gallery.
     * @param requestCode code that the image activity was launched with
     * @param resultCode code that the image activity returns
     * @param data data from the intent
     */
    public void onActivityResult(Activity activity, int fieldId, int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        // if the user launched the camera
        if (requestCode == LAUNCH_CAMERA_CODE) {
            if(resultCode == Activity.RESULT_OK) {  // if a photo was successfully chosen
                // set the profile picture ImageView to the chosen image
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ImageView myImage = (ImageView) activity.findViewById(fieldId);
                myImage.setImageBitmap(myBitmap);
                showRemovePhoto = true;  // show Remove Photo option since user now has a photo
                galleryPhoto = null;  // nullify the gallery photo variable
            } else if (resultCode == Activity.RESULT_CANCELED) {  // if the activity was cancelled
                Log.e("SIGN_UP_ACTIVITY", "Camera failed!");
            }
        } else if (requestCode == LAUNCH_GALLERY_CODE) {  // if the user launched the gallery
            if(resultCode == Activity.RESULT_OK) {  // if a photo was successfully selected
                try {  // try to get a Bitmap of the selected image
                    InputStream inputStream = activity.getBaseContext()
                            .getContentResolver()
                            .openInputStream(Objects.requireNonNull(data.getData()));
                    // store the selected image in galleryPhoto
                    galleryPhoto = BitmapFactory.decodeStream(inputStream);
                    currentPhotoPath = "BITMAP";
                    ImageView myImage = (ImageView) activity.findViewById(fieldId);
                    myImage.setImageBitmap(galleryPhoto);
                    showRemovePhoto = true;  // show Remove Photo option since user now has a photo

                } catch (FileNotFoundException e) {  // handle when the selected image is not found
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {  // if the activity was cancelled
                Log.e("SIGN_UP_ACTIVITY", "Failed Gallery!");
            }
        }
    }

    /**
     * Getter method for currentPhotoPath
     * @return currentPhotoPath as String
     */
    public String getCurrentPhotoPath() {
        return this.currentPhotoPath;
    }

    /**
     * Getter method for galleryPhoto
     * @return galleryPhoto as String
     */
    public Bitmap getGalleryPhoto() {
        return galleryPhoto;
    }

    /**
     * Setter method for showRemovePhoto
     */
    public void setShowRemovePhoto(Boolean showRemovePhoto) {
        this.showRemovePhoto = showRemovePhoto;
    }
}
