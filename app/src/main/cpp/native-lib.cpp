#include <jni.h>
#include <string>
#include <sstream>

#include "ed25519.h"

#include "ge.h"
#include "sc.h"

unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_dpass_android_activities_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_dpass_android_activities_MainActivity_generateKeyPair(
        JNIEnv *env,
        jobject /* this */,
        const jbyteArray seed) {

    unsigned char public_key[32], private_key[64];

    ed25519_create_keypair(public_key, private_key, as_unsigned_char_array(env, seed));

    std::ostringstream buffer_pub;
    for(int i = 0; i < 50; ++i)
        buffer_pub << public_key[i] << " ";
    std::string pub = buffer_pub.str();

    std::ostringstream buffer_pri;
    for(int i = 0; i < 50; ++i)
        buffer_pri << private_key[i] << " ";
    std::string pri = buffer_pri.str();

    return env->NewStringUTF(pub.c_str());
}