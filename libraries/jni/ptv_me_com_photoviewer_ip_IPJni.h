/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ptv_me_com_photoviewer_ip_IPJni */

#ifndef _Included_ptv_me_com_photoviewer_ip_IPJni
#define _Included_ptv_me_com_photoviewer_ip_IPJni
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ptv_me_com_photoviewer_ip_IPJni
 * Method:    helloJNI
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_helloJNI
  (JNIEnv *, jobject, jstring);
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_processImage
        (JNIEnv *, jobject, jlong, jlong);
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_histEqualize
        (JNIEnv *, jobject, jlong, jlong);
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_faceDetect
        (JNIEnv *, jobject, jlong, jlong, jstring, jstring);
JNIEXPORT void JNICALL Java_ptv_me_com_photoviewer_ip_IPJni_denoising
        (JNIEnv *, jobject, jlong, jlong);
#ifdef __cplusplus
}
#endif
#endif
