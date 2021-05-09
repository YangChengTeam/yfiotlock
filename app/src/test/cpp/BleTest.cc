#include <gtest/gtest.h>

extern "C" {
#include "../../main/cpp/cr4.h"

}



TEST(CR4Test, ENCRYT) {
    unsigned char *AES_KEY = (uint8_t *) "12345678";
    unsigned char *in = (unsigned char *) "a";
    struct rc4_key s;
    prepare_key(AES_KEY, sizeof(AES_KEY), &s);
    rc4(in, sizeof(in), &s);
    ASSERT_EQ(in[0], 0x1A);
}