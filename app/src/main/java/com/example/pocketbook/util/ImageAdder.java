package com.example.pocketbook.util;

import android.content.Context;
import android.graphics.Camera;

/*
       Interface to add Images
       To be used throughout other Image Adding functionalities
 */
public interface ImageAdder {

    public void openCamera();
    public void selectImage(Context context);
    public boolean uploadImageToFirebase();

}
