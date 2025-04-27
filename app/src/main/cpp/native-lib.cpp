#include <jni.h>
#include <Eigen/Dense>
#include <android/log.h>

#define LOG_TAG "NativeMatrixOps"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

using namespace Eigen;

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matop_MainActivity_addMatrices(
        JNIEnv *env, jobject /* this */,
        jfloatArray arrA, jfloatArray arrB) {
    LOGI("addMatrices called");

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

extern "C" JNIEXPORT void JNICALL
Java_com_example_matop_MainActivity_isMatrixInvertible(
        JNIEnv *env, jobject /* this */,
        jfloatArray arrB, jint n) {
    LOGI("checkMatrixInvertible called (n=%d)", n);

    if (n <= 0) {
        LOGI("checkMatrixInvertible: invalid dimension");
        jclass iae = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(iae, "Matrix dimension must be > 0");
        return;
    }

    jfloat* b = env->GetFloatArrayElements(arrB, nullptr);
    Map<MatrixXf> matB(b, n, n);

    env->ReleaseFloatArrayElements(arrB, b, JNI_ABORT);

    FullPivLU<MatrixXf> lu(matB);
    if (!lu.isInvertible()) {
        LOGI("checkMatrixInvertible: matrix is singular");
        jclass re = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(re, "Matrix is singular (not invertible)");
        return;
    }

    LOGI("checkMatrixInvertible: matrix is invertible");
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matop_MainActivity_invertMatrix(
        JNIEnv* env, jobject /* this */,
        jfloatArray arrB, jint n) {
    LOGI("invertMatrix called (n=%d)", n);

    jboolean isCopy = JNI_FALSE;
    jfloat* b = env->GetFloatArrayElements(arrB, &isCopy);

    Map< Matrix<float, Dynamic, Dynamic, RowMajor> > matB(b, n, n);

    LOGI("invertMatrix: computing inverse");
    MatrixXf invB = matB.inverse();

    const int   size = n * n;
    jfloatArray out  = env->NewFloatArray(size);

    env->SetFloatArrayRegion(out, 0, size, invB.data());

    env->ReleaseFloatArrayElements(arrB, b, JNI_ABORT);
    LOGI("invertMatrix: returning result");
    return out;
}