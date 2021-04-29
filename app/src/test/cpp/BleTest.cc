#include <gtest/gtest.h>

extern "C" {
#include "../../main/cpp/aes.h"
}


TEST(BleTest, NotEmpty) {
    const uint8_t *AES_KEY = (uint8_t *) "12345678";
    const char *in = "a";
    char *result = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);
    ASSERT_EQ(result[0], 0x1A);
}


