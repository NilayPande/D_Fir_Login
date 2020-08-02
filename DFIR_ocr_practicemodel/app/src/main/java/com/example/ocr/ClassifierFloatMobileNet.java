package com.example.ocr;


import android.app.Activity;
import android.view.textclassifier.TextClassification;

import org.tensorflow.lite.support.model.Model;

import java.io.IOException;

public class ClassifierFloatMobileNet extends Classifier {
    protected ClassifierFloatMobileNet(Activity activity, Model.Device device, int numThreads) throws IOException {
        super(activity, device, numThreads);
    }

    @Override
    protected String getModelPath() {
        return "saved_model.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "en_50k.txt";
    }

}
