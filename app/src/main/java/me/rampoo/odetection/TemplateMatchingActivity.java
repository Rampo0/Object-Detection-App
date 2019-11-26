package me.rampoo.odetection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

import okhttp3.internal.Util;

public class TemplateMatchingActivity extends Activity implements CvCameraViewListener2 {

    private static final String TAG = "TMAct";

    private Mat mRgba;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat templ;
    private Mat sourceImage;
    private Mat outputMatching;
    private int image = R.drawable.pxl;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.d(TAG, "OpenCV loaded successfully");

                    // load image source
                    try {
                        sourceImage = Utils.loadResource(getApplicationContext(), image , Imgcodecs.CV_LOAD_IMAGE_COLOR);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                    // convert the image to gray
                     templ = new Mat();
                     Imgproc.cvtColor(sourceImage, templ, Imgproc.COLOR_BGR2GRAY);

                    // convert the image to rgba
                    // templ = new Mat();
                    // Imgproc.cvtColor(sourceImage, templ, Imgproc.COLOR_BGR2RGBA);

                    outputMatching = new Mat();

                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_matching);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.template_matching_cam);
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

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Mat gray_img = new Mat();

        Imgproc.cvtColor(mRgba, gray_img, Imgproc.COLOR_BGR2GRAY);

        /// Create the result matrix
        int result_cols =  mRgba.cols() - templ.cols() + 1;
        int result_rows = mRgba.rows() - templ.rows() + 1;
        outputMatching.create(result_rows, result_cols, CvType.CV_32FC1);

        // do template matching
        // int matchMethod = Imgproc.TM_SQDIFF;
        int matchMethod = Imgproc.TM_CCOEFF;
        Imgproc.matchTemplate(gray_img, templ , outputMatching , matchMethod);

        // normalize
        Core.normalize(outputMatching, outputMatching, 0, 10, Core.NORM_MINMAX, -1, new Mat());

        // get detection location
        Core.MinMaxLocResult mmLocationResult = Core.minMaxLoc(outputMatching);
        Point location = null;
        Double matchScore = null;

        if( matchMethod  == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED )
        {
            location = mmLocationResult.minLoc;
            matchScore = mmLocationResult.minVal;
        }
        else
        {
            location = mmLocationResult.maxLoc;
            matchScore = mmLocationResult.maxVal;
        }

        Log.i(TAG , "Score : " + matchScore.toString());
        Double threshold = 0.9d;

        // draw rectangle on cam
        if(matchScore > threshold) {
            Imgproc.rectangle(mRgba, location, new Point(location.x + templ.rows(), location.y + templ.rows()), new Scalar(0, 255, 0), 5);
        }
        // draw circle on cam
        // Imgproc.circle(mRgba , new Point(location.x + (templ.rows() / 2) , location.y + (templ.rows() / 2)) , (int)templ.rows() ,  new Scalar(0,255,0), 10);

        return mRgba;

    }

}
