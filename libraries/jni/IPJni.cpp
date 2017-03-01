//
// Created by my on 17. 2. 28.
//

#include "ptv_me_com_photoviewer_ip_IPJni.h"
#include <string>
#include <sstream>
#include <vector>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgcodecs/imgcodecs.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/photo.hpp>
#include <android/log.h>

#define LOG_TAG "IPTAG"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

JNIEXPORT jstring JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_helloJNI
  (JNIEnv *env, jobject, jstring jname)
{
    const char * name = env->GetStringUTFChars(jname, NULL);
    std::string str = "hello ";
    std::stringstream ss;
    ss << str << name;

    return env->NewStringUTF(ss.str().c_str());

}
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_processImage
(JNIEnv *env, jobject,jlong jin, jlong jout)
{
    cv::Mat* in = (cv::Mat*)jin;
    cv::Mat* out = (cv::Mat*)jout;


    try
    {
        cv::cvtColor(*in, *out, CV_BGR2GRAY);

        cv::blur(*in, *out, cv::Size(3, 3));

        cv::Canny(*out, *out, 100, 200, 3);
    }
    catch(cv::Exception& e)
    {
        LOGD("processImage caught cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je)
            je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
    }



}
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_histEqualize
(JNIEnv *env, jobject, jlong jin, jlong jout)
{
    cv::Mat* in = (cv::Mat*)jin;
    cv::Mat* out = (cv::Mat*)jout;


    try
    {
        cv::cvtColor(*in, *out,CV_BGR2YCrCb);
        std::vector<cv::Mat> channels;

        cv::split(*out,channels);
        cv::equalizeHist(channels[0], channels[0]);
        cv::Mat result;

        cv::merge(channels, *out);

        cv::cvtColor(*out, *out, CV_YCrCb2BGR);

    }
    catch(cv::Exception& e)
    {
        LOGD("processImage caught cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je)
            je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
    }
}
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_faceDetect
 (JNIEnv *env, jobject, jlong jin, jlong jout, jstring jface_cascade_path, jstring jeye_cascade_path)
{
    cv::Mat* in = (cv::Mat*)jin;
    cv::Mat* out = (cv::Mat*)jout;

    *out = in->clone();

    try
    {
        const char * face_cascade_path = env->GetStringUTFChars(jface_cascade_path, NULL);
        const char * eye_cascade_path = env->GetStringUTFChars(jeye_cascade_path, NULL);

        cv::CascadeClassifier face_cascade;
        cv::CascadeClassifier eye_cascade;
        face_cascade.load(face_cascade_path);
        eye_cascade.load(eye_cascade_path);

        cv::Mat gray;
        cv::cvtColor(*in, gray,CV_BGR2GRAY);

        cv::equalizeHist( gray, gray);
        std::vector<cv::Rect> faces;
        face_cascade.detectMultiScale( gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, cv::Size(30, 30) );
      for( size_t i = 0; i < faces.size(); i++ )
      {
        cv::Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
        cv::ellipse( *out, center, cv::Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, cv::Scalar( 255, 0, 255 ), 4, 8, 0 );

        cv::Mat faceROI = gray( faces[i] );
        std::vector<cv::Rect> eyes;

        //-- In each face, detect eyes
        eye_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, cv::Size(30, 30) );

        for( size_t j = 0; j < eyes.size(); j++ )
         {
           cv::Point center( faces[i].x + eyes[j].x + eyes[j].width*0.5, faces[i].y + eyes[j].y + eyes[j].height*0.5 );
           int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
           cv::circle( *out, center, radius, cv::Scalar( 255, 0, 0 ), 4, 8, 0 );
         }
      }

    }
    catch(cv::Exception& e)
    {
        LOGD("processImage caught cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je)
            je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
    }
}
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_denoising
 (JNIEnv *env, jobject, jlong jin, jlong jout)
{
    cv::Mat* in = (cv::Mat*)jin;
    cv::Mat* out = (cv::Mat*)jout;

    try
    {
        cv::fastNlMeansDenoisingColored(*in, *out);

    }
    catch(cv::Exception& e)
    {
        LOGD("processImage caught cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je)
            je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
    }
}
