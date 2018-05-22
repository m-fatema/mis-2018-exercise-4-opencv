/*
    @Purpose:  To detect nose and indicate it with the help of classifier
    @Created Date: 15 May, 2018
 */

package com.example.mis.opencv;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private CascadeClassifier mFaceDetector, mNoseDetector;
    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    Mat gray,col;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    String filePath = initAssetFile("haarcascade_frontalface_default.xml");
                    mOpenCvCameraView.enableView();
                    mFaceDetector = new CascadeClassifier( filePath );
                    if( mFaceDetector.empty() ){
                        Log.d(TAG, "Failed to load face cascade classifier");
                        mFaceDetector = null;
                    }
                    else{
                        String noseFilePath = initAssetFile("haarcascade_nose.xml");
                        mNoseDetector = new CascadeClassifier( noseFilePath );
                        if( mNoseDetector.empty() ){
                            Log.d(TAG, "Failed to load nose cascade classifier");
                            mNoseDetector = null;
                        }
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        gray = new Mat();
        col = new Mat();
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        int height;
        gray = inputFrame.gray();
        col  = inputFrame.rgba();

        Mat tmp = gray.clone();
        //Imgproc.Canny(gray, tmp, 80, 100);
        //Imgproc.cvtColor(tmp, col, Imgproc.COLOR_GRAY2RGBA, 4);
        //Core.flip(col, col, 1);
        /*
        *********************************************************************************
        http://romanhosek.cz/android-eye-detection-updated-for-opencv-2-4-6/
        */
        height = gray.rows();
        if (mAbsoluteFaceSize == 0) {
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
        MatOfRect nose = new MatOfRect();

        if (mFaceDetector != null)
            mFaceDetector.detectMultiScale(gray, faces, 1.1, 2,
                    2,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
                    new Size()); //minNeighbours = accuracy = minimum for deciding threshold

        Rect[] faceArray = faces.toArray();

        for (Rect face : faceArray) {
            Imgproc.rectangle(col, face.tl(), face.br(),new Scalar(0, 0, 255, 0), 4);
            Rect faceRect = new Rect((int) face.tl().x, (int) (face.tl().y), face.width, (face.height));
            Mat faceGray = gray.submat(faceRect);
            Mat faceRgb = col.submat(faceRect);

            //https://hackprojects.wordpress.com/tutorials/opencv-python-tutorials/opencv-nose-detection-using-haar-cascades/
            mNoseDetector.detectMultiScale(faceGray, nose, 1.1, 2, 2,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

            Rect[] nosesArray = nose.toArray();
            for (Rect n : nosesArray) {

                //Point center = new Point(n.width , n.height );
                Point center = new Point(n.x + n.width * 0.5 , n.y + n.height *0.36 );

                //Radius set with trial & test method
                int radius = (int) (n.width * 0.25);
                Imgproc.circle(faceRgb, center, radius, new Scalar(255, 0, 0, 255), -1);
            }
        }



//        for (int i = 0; i < faceArray.length; i++) {
//            Imgproc.rectangle(col, faceArray[i].tl(), faceArray[i].br(),new Scalar(0, 0, 255, 0), 4);
//
//            xCenter = (faceArray[i].x + faceArray[i].x + faceArray[i].width) / 2;
//            yCenter = (faceArray[i].y + faceArray[i].y + faceArray[i].height) / 2;
//
//            Point center = new Point(xCenter, yCenter);
//            int radius = 10;//(int)(faceArray[i].height / 10 );
//            Log.d(TAG, "Radius[" + String.valueOf(i) + "]" +  String.valueOf(radius));
//            Imgproc.circle(col, center, radius, new Scalar(255, 0, 0, 255), -1);
//        }

        //*********************************************************************************
        return col;
    }


    public String initAssetFile(String filename)  {
        File file = new File(getFilesDir(), filename);
        if (!file.exists()) try {
            InputStream is = getAssets().open(filename);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data); os.write(data);
            is.close(); os.close();
        } catch (IOException e){e.printStackTrace();Log.d(TAG,"prepared local file ERROR: "+e.getMessage());}

        Log.d(TAG,"prepared local file: "+file.getAbsolutePath());
        return file.getPath();
    }
}
