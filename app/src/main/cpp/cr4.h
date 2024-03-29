
#include <stdint.h>
#include <stdio.h>
#define buf_size 1024
typedef struct rc4_key
{
    unsigned char state[256];
    unsigned char x;
    unsigned char y;
} rc4_key;

void rc4(unsigned char* buffer_ptr, int buffer_len, rc4_key* key);
void prepare_key(unsigned char* key_data_ptr, int key_data_len, rc4_key* key);