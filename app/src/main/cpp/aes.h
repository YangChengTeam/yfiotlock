#ifndef _AES_H_
#define _AES_H_

#include <stdlib.h>
#include <stdint.h>

// #define the macros below to 1/0 to enable/disable the mode of operation.
//
// CBC enables AES128 encryption in CBC-mode of operation and handles 0-padding.
// ECB enables the basic ECB 16-byte block algorithm. Both can be enabled simultaneously.

// The #ifndef-guard allows it to be configured before #include'ing or at compile time.
#ifndef CBC
#define CBC 1
#endif

#ifndef ECB
#define ECB 1
#endif

static const unsigned char HEX[16] = {0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                                      0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

#if defined(ECB) && ECB

void AES128_ECB_encrypt(uint8_t *input, const uint8_t *key, uint8_t *output);

void AES128_ECB_decrypt(uint8_t *input, const uint8_t *key, uint8_t *output);

char *AES_128_ECB_PKCS5Padding_Encrypt(const char *in, const uint8_t *key);

char *AES_128_ECB_PKCS5Padding_Decrypt(const char *in, const uint8_t *key);

int findPaddingIndex(uint8_t *str);

#endif

#endif //_AES_H_