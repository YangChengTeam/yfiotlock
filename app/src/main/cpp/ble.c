//
// Created by Administrator on 2021/2/22.
//
#include <jni.h>
#include <string.h>
#include <sys/ptrace.h>
#include "ble.h"
#include "crc16.h"
#include "aes.h"


jstring charToJstring(JNIEnv *envPtr, char *src) {
    JNIEnv env = *envPtr;

    jsize len = strlen(src);
    jclass clsstring = env->FindClass(envPtr, "java/lang/String");
    jstring strencode = env->NewStringUTF(envPtr, "UTF-8");
    jmethodID mid = env->GetMethodID(envPtr, clsstring, "<init>",
                                     "([BLjava/lang/String;)V");
    jbyteArray barr = env->NewByteArray(envPtr, len);
    env->SetByteArrayRegion(envPtr, barr, 0, len, (jbyte *) src);

    return (jstring) env->NewObject(envPtr, clsstring, mid, barr, strencode);
}



JNIEXPORT jstring JNICALL encode(JNIEnv *env, jobject instance, jobject context, jstring  key, jbyteArray data) {
    uint8_t *AES_KEY = (uint8_t *)(*env)->GetStringUTFChars(env, key, 0);
    const char *in = (char *) (*env)->GetByteArrayElements(env, data, NULL);
    char *baseResult = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);
    (*env)->ReleaseStringUTFChars(env, key, AES_KEY);
    (*env)->ReleaseByteArrayElements(env, data, in, 0);
    return (*env)->NewStringUTF(env, baseResult);
}


JNIEXPORT jstring JNICALL decode(JNIEnv *env, jobject instance, jobject context, jstring  key, jbyteArray data) {
    uint8_t *AES_KEY = (uint8_t *)(*env)->GetStringUTFChars(env, key, 0);
    const char *in = (char *) (*env)->GetByteArrayElements(env, data, NULL);
    char *desResult = AES_128_ECB_PKCS5Padding_Decrypt(in, AES_KEY);
    (*env)->ReleaseStringUTFChars(env, key, AES_KEY);
    (*env)->ReleaseByteArrayElements(env, data, in, 0);
    return charToJstring(env, desResult);
}

static JNINativeMethod method_table[] = {
        {"crc16", "([BI)I", (void *) crc16tablefast},
        {"decode", "(Ljava/lang/Object;Ljava/lang/String;[B)Ljava/lang/String;", (void *) decode},
        {"encode", "(Ljava/lang/Object;Ljava/lang/String;[B)Ljava/lang/String;", (void *) encode}
};


static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}


int register_ndk_load(JNIEnv *env) {
    // 调用注册方法
    return registerNativeMethods(env, JNIREG_CLASS,
                                 method_table, NELEM(method_table));
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    register_ndk_load(env);

    return JNI_VERSION_1_4;
}