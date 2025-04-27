#include <jni.h>
#include <Eigen/Dense>
#include <android/log.h>

#define LOG_TAG "NativeMatrixOps"  // Tag for all your logs
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)  // Info level log

using namespace Eigen;

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matop_MainActivity_addMatrices(
        JNIEnv *env, jobject /* this */,
        jfloatArray arrA, jfloatArray arrB) {
    LOGI("addMatrices called"); // Log when function is invoked

    jfloat *a = env->GetFloatArrayElements(arrA, nullptr);
    jfloat *b = env->GetFloatArrayElements(arrB, nullptr);
    jsize len = env->GetArrayLength(arrA);

    Map<VectorXf> vecA(a, len);
    Map<VectorXf> vecB(b, len);

    VectorXf result = vecA + vecB;

    jfloatArray out = env->NewFloatArray(len);
    env->SetFloatArrayRegion(out, 0, len, result.data());

    env->ReleaseFloatArrayElements(arrA, a, JNI_ABORT);
    env->ReleaseFloatArrayElements(arrB, b, JNI_ABORT);

    return out;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matop_MainActivity_subtractMatrices(
        JNIEnv *env, jobject /* this */,
        jfloatArray arrA, jfloatArray arrB) {
    LOGI("subtractMatrices called");

    jfloat *a = env->GetFloatArrayElements(arrA, nullptr);
    jfloat *b = env->GetFloatArrayElements(arrB, nullptr);
    jsize len = env->GetArrayLength(arrA);

    Map<VectorXf> vecA(a, len);
    Map<VectorXf> vecB(b, len);

    VectorXf result = vecA - vecB;

    jfloatArray out = env->NewFloatArray(len);
    env->SetFloatArrayRegion(out, 0, len, result.data());

    env->ReleaseFloatArrayElements(arrA, a, JNI_ABORT);
    env->ReleaseFloatArrayElements(arrB, b, JNI_ABORT);

    return out;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matop_MainActivity_multiplyMatrices(
        JNIEnv *env, jobject /* this */,
        jfloatArray arrA, jfloatArray arrB,
        jint n, jint m, jint p) {
    LOGI("multiplyMatrices called");

    jfloat *a = env->GetFloatArrayElements(arrA, nullptr);
    jfloat *b = env->GetFloatArrayElements(arrB, nullptr);

    Map<MatrixXf> matA(a, n, m);
    Map<MatrixXf> matB(b, m, p);

    MatrixXf result = matA * matB;

    jfloatArray out = env->NewFloatArray(n * p);
    env->SetFloatArrayRegion(out, 0, n * p, result.data());

    env->ReleaseFloatArrayElements(arrA, a, JNI_ABORT);
    env->ReleaseFloatArrayElements(arrB, b, JNI_ABORT);

    return out;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matop_MainActivity_divideMatrices(
        JNIEnv *env, jobject /* this */,
        jfloatArray arrA, jfloatArray arrB) {
    LOGI("divideMatrices called");

    jfloat *a = env->GetFloatArrayElements(arrA, nullptr);
    jfloat *b = env->GetFloatArrayElements(arrB, nullptr);
    jsize len = env->GetArrayLength(arrA);

    Map<VectorXf> vecA(a, len);
    Map<VectorXf> vecB(b, len);

    VectorXf result = vecA.array() / vecB.array();

    jfloatArray out = env->NewFloatArray(len);
    env->SetFloatArrayRegion(out, 0, len, result.data());

    env->ReleaseFloatArrayElements(arrA, a, JNI_ABORT);
    env->ReleaseFloatArrayElements(arrB, b, JNI_ABORT);

    return out;
}
