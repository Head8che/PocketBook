package com.example.pocketbook.util;

import android.content.Context;
import android.graphics.Camera;

public interface ImageAdder {

    public void openCamera();
    public void selectImage(Context context);
    public boolean uploadImageToFirebase();

}
