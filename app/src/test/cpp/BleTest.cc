#include <gtest/gtest.h>

extern "C" {
#include "../../main/cpp/aes.h"
}


TEST(BleTest, NotEmpty) {
    const uint8_t *AES_KEY = (uint8_t *) "2b7e151628aed2a6abf7158809cf4f3c";
    const char *in = "a";
    char *result = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);
    ASSERT_EQ(result[0], 0x3F);
}


