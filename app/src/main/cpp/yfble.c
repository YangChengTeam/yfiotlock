
#include <jni.h>
#include <string.h>
#include <sys/ptrace.h>
#include "yfble.h"
#include "cr4.h"



JNIEXPORT jbyteArray JNICALL encrypt(JNIEnv *env,  jclass clazz, jstring  key, jbyteArray data) {
    return null;
}


JNIEXPORT jbyteArray JNICALL decrypt(JNIEnv *env, jclass clazz, jstring  key, jbyteArray data) {
    return null;
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