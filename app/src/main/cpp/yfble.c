
#include <jni.h>
#include <string.h>
#include <sys/ptrace.h>
#include "yfble.h"
#include "cr4.h"


jbyteArray char2byteArray(JNIEnv *envPtr, char *src) {

    JNIEnv env = *envPtr;

    jsize len = strlen(src);
    jbyteArray barr = env->NewByteArray(envPtr, len);

    env->SetByteArrayRegion(envPtr, barr, 0, len, (jbyte *) src);

    return barr;
}

jbyteArray as_byte_array(JNIEnv *envPtr, unsigned char* buf, int len) {
    JNIEnv env = *envPtr;
    jbyteArray array = env->NewByteArray (env, len);
    env->SetByteArrayRegion (env, array, 0, len, buf);
    return array;
}

unsigned char* as_unsigned_char_array(JNIEnv *envPtr, jbyteArray array) {
    JNIEnv env = *envPtr;
    int len = env->GetArrayLength (env, array);
    unsigned char* buf[len];
    memset(buf,0, len);
    env->GetByteArrayRegion (env, array, 0, len, buf);
    return buf;
}



JNIEXPORT jbyteArray JNICALL encrypt(JNIEnv *env,  jclass clazz, jstring  key, jbyteArray data) {

    unsigned char  *AES_KEY = (unsigned char *)(*env)->GetByteArrayElements(env, data, NULL);
    unsigned char *in = (unsigned char *)(*env)->GetByteArrayElements(env, data, NULL);
    struct rc4_key s;
    prepare_key(AES_KEY, sizeof(AES_KEY), &s);
    rc4(in, sizeof(in), &s);
    return as_unsigned_char_array(env, in);
}


JNIEXPORT jbyteArray JNICALL decrypt(JNIEnv *env, jclass clazz, jstring  key, jbyteArray data) {

    unsigned char  *AES_KEY = (unsigned char *)(*env)->GetByteArrayElements(env, data, NULL);
    unsigned char *in = (char *) (*env)->GetByteArrayElements(env, data, NULL);

    struct rc4_key s;
    prepare_key(AES_KEY, sizeof(AES_KEY), &s);
    rc4(in, sizeof(in), &s);
    return as_unsigned_char_array(env, in);
}

static JNINativeMethod method_table[] = {
        {"decrypt", "(Ljava/lang/String;[B)[B", (void *) decrypt},
        {"encrypt", "(Ljava/lang/String;[B)[B", (void *) encrypt}
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
    return registerNativeMethods(env, JNIREG_CLASS,
                                 method_table, NELEM(method_table));
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = JNI_ERR;

    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    register_ndk_load(env);

    return JNI_VERSION_1_4;
}