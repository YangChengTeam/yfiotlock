#ifndef YF_IOT_LOCK_YFBLE_H
#define YF_IOT_LOCK_YFBLE_H

#include <android/log.h>

#ifndef LOGE
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "yfble",__VA_ARGS__)
#endif

# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
# define JNIREG_CLASS "com/yc/yfiotlock/ble/LockBLEUtil"

#endif //YF_IOT_LOCK_YFBLE_H
