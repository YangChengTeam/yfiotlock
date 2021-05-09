
#include "cr4.h"


void swap_byte(unsigned char* x, unsigned char* y)
{
    *x = *x ^ *y;
    *y = *x ^ *y;
    *x = *x ^ *y;
}
void prepare_key(unsigned char* key_data_ptr, int key_data_len, rc4_key* key)
{
    int i;
    unsigned char t;
    unsigned char swapByte;
    unsigned char index1;
    unsigned char index2;
    unsigned char* state;
    short counter;
    state = &key->state[0];
    for (counter = 0; counter < 256; counter++)
        state[counter] = counter;
    key->x = 0;
    key->y = 0;
    index1 = 0;
    index2 = 0;
    for (counter = 0; counter < 256; counter++)
    {
        index2 = (key_data_ptr[index1] + state[counter] + index2) % 256;
        swap_byte(&state[counter], &state[index2]);
        index1 = (index1 + 1) % key_data_len;
    }
}
void rc4(unsigned char* buffer_ptr, int buffer_len, rc4_key* key)
{
    unsigned char t;
    unsigned char x;
    unsigned char y;
    unsigned char* state;
    unsigned char xorIndex;
    short counter;
    x = key->x;
    y = key->y;
    state = &key->state[0];
    for (counter = 0; counter < buffer_len; counter++)
    {
        x = (x + 1) % 256;
        y = (state[x] + y) % 256;
        swap_byte(&state[x], &state[y]);
        xorIndex = (state[x] + state[y]) % 256;
        buffer_ptr[counter] ^= state[xorIndex];
    }
    key->x = x;
    key->y = y;
}


void PrintBuffer(void* pBuff, unsigned int nLen)
{
    if (NULL == pBuff || 0 == nLen)
    {
        return;
    }

    const int nBytePerLine = 16;
    unsigned char* p = (unsigned char*)pBuff;
    char szHex[3 * nBytePerLine + 1] = { 0 };

    //printf("-----------------begin-------------------\n");
    for (unsigned int i = 0; i < nLen; ++i)
    {
        int idx = 3 * (i % nBytePerLine);
        if (0 == idx)
        {
            memset(szHex, 0, sizeof(szHex));
        }
//#ifdef WIN32
        //sprintf_s(&szHex[idx], 4, "%02x ", p[i]);// buff长度要多传入1个字节
//#else
        snprintf(&szHex[idx], 4, "%02x ", p[i]); // buff长度要多传入1个字节
//#endif

        // 以16个字节为一行，进行打印
        if (0 == ((i + 1) % nBytePerLine))
        {
            printf("%s\n", szHex);
        }
    }

    // 打印最后一行未满16个字节的内容
    if (0 != (nLen % nBytePerLine))
    {
        printf("%s\n", szHex);
    }

    // printf("------------------end-------------------\n");
}


int main()
{
    struct rc4_key s;
    unsigned char key[] = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x08 };
    unsigned char Key_Len = 6;
    unsigned char myinput[17];
    unsigned char temp[] = {0x02, 0x39, 0x98, 0x78, 0xAB, 0x78, 0xbc, 0x98};  //"nankai university\0";
    unsigned char iLen = 8;
    int i = 0;

    memset(myinput, 0, 17);

    for (i = 0; i < iLen; i++)
    {
        myinput[i] = *(temp + i);
    }
    printf("orignal text:\n");

    PrintBuffer(myinput, iLen);

    //加密
    prepare_key(key, Key_Len, &s);
    rc4(myinput, iLen, &s);

    printf("secret string:\n");
    PrintBuffer(myinput, iLen);



    printf("\ndecrypt the secret string,the result is:\n");

    //解密
    prepare_key(key, Key_Len, &s);
    rc4(myinput, iLen, &s);
    PrintBuffer(myinput, iLen);

    return 1;
}