#include<stdio.h>
#include<string.h>
#include<stdlib.h>
void g_print(char *s) {
    printf("%s", s);
}
void g_println(char *s) {
    printf("%s\n", s);
}
void g_printInt(int v) {
    printf("%d", v);
}
void g_printlnInt(int v) {
    printf("%d\n", v);
}
char *g_getString() {
    char *tmp = malloc(sizeof(char) * 1000);
    scanf("%s", tmp);
    return tmp;
}
int g_getInt() {
    int ipt;
    scanf("%d", &ipt);
    return ipt;
}
char * g_toString(int i) {
    if (i == 0){
        char *r = (char *)malloc(sizeof(char) * 2);
        r[0] = '0'; r[1] = '\0';
        return r;
    }
    short digits[10];
    short neg = i < 0;
    if (neg) i=-i;
    short len = 0;
    while (i>0){
        digits[len++] = i%10;
        i/=10;
    }
    char *r = (char *)malloc(sizeof(char) * (len + neg + 1));
    short p = 0;
    if (neg) r[0] = '-';
    while (p < len){
        r[p + neg] = digits[len - p - 1] + '0';
        ++p;
    }
    r[len + neg] = '\0';
    return r;
}
int l_string_length(char *s) {
    return (int)strlen(s);
}
char * l_string_substring(char *it, int left, int right) {
    char *tmp = malloc(sizeof(char) * 1000);
    return memcpy(tmp, it + left, right - left);
    tmp[right - left] = '\0';
    return tmp;
}

int l_string_parseInt(char *it) {
    int ret;
    sscanf(it, "%d", &ret);
    return ret;
}
int l_string_ord(char *it, int pos) {
    return (int)it[pos];
}
char *g_stringAdd(char *a, char *b) {
    char *tmp = malloc(sizeof(char) * 1000);
    int len = strlen(a);
    memcpy(tmp, a, len);
    tmp[len] = '\0';
    strcat(tmp, b);
    return tmp;
}
int g_stringLT(char *a, char *b) {
    return strcmp(a, b) < 0;
}
int g_stringGT(char *a, char *b) {
    return strcmp(a, b) > 0;
}
int g_stringLE(char *a, char *b) {
    return strcmp(a, b) <= 0;
}
int g_stringGE(char *a, char *b) {
    return strcmp(a, b) >= 0;
}
int g_stringEQ(char *a, char *b) {
    return strcmp(a, b) == 0;
}
int g_stringNE(char *a, char *b) {
    return strcmp(a, b) != 0;
}