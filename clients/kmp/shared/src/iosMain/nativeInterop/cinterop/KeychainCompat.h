#import <CoreFoundation/CoreFoundation.h>
#import <Foundation/Foundation.h>
#import <Security/Security.h>
#include <string.h>

static inline CFMutableDictionaryRef convx_keychain_query(const char *account) {
    if (account == NULL) return NULL;
    CFStringRef serviceString = CFStringCreateWithCString(
        kCFAllocatorDefault, "com.convx.music.youtube", kCFStringEncodingUTF8
    );
    CFStringRef accountString = CFStringCreateWithCString(
        kCFAllocatorDefault, account, kCFStringEncodingUTF8
    );
    CFMutableDictionaryRef query = CFDictionaryCreateMutable(
        kCFAllocatorDefault,
        0,
        &kCFTypeDictionaryKeyCallBacks,
        &kCFTypeDictionaryValueCallBacks
    );
    if (serviceString == NULL || accountString == NULL || query == NULL) {
        if (serviceString != NULL) CFRelease(serviceString);
        if (accountString != NULL) CFRelease(accountString);
        if (query != NULL) CFRelease(query);
        return NULL;
    }
    CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword);
    CFDictionarySetValue(query, kSecAttrService, serviceString);
    CFDictionarySetValue(query, kSecAttrAccount, accountString);
    CFRelease(serviceString);
    CFRelease(accountString);
    return query;
}

static inline int convx_keychain_read(
    const char *account,
    char *buffer,
    size_t capacity
) {
    if (buffer == NULL || capacity == 0) return (int)errSecParam;
    CFMutableDictionaryRef query = convx_keychain_query(account);
    if (query == NULL) return (int)errSecAllocate;
    CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue);
    CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne);
    CFDataRef value = NULL;
    OSStatus status = SecItemCopyMatching(query, (CFTypeRef *)&value);
    CFRelease(query);
    if (status != errSecSuccess) return (int)status;
    if (value == NULL) return (int)errSecDecode;

    CFIndex dataLength = CFDataGetLength(value);
    if ((size_t)dataLength > capacity) {
        CFRelease(value);
        return (int)errSecBufferTooSmall;
    }
    const UInt8 *bytes = CFDataGetBytePtr(value);
    if (dataLength > 0 && bytes == NULL) {
        CFRelease(value);
        return (int)errSecDecode;
    }
    if (dataLength > 0) memcpy(buffer, bytes, (size_t)dataLength);
    CFRelease(value);
    return (int)dataLength;
}

static inline int convx_keychain_write(
    const char *account,
    const char *bytes,
    size_t length
) {
    if (bytes == NULL || length == 0) return (int)errSecParam;
    CFMutableDictionaryRef query = convx_keychain_query(account);
    if (query == NULL) return (int)errSecAllocate;
    CFDataRef value = CFDataCreate(kCFAllocatorDefault, (const UInt8 *)bytes, (CFIndex)length);
    if (value == NULL) {
        CFRelease(query);
        return (int)errSecAllocate;
    }
    CFMutableDictionaryRef updates = CFDictionaryCreateMutable(
        kCFAllocatorDefault,
        0,
        &kCFTypeDictionaryKeyCallBacks,
        &kCFTypeDictionaryValueCallBacks
    );
    if (updates == NULL) {
        CFRelease(value);
        CFRelease(query);
        return (int)errSecAllocate;
    }
    CFDictionarySetValue(updates, kSecValueData, value);

    OSStatus status = SecItemUpdate(query, updates);
    if (status == errSecItemNotFound) {
        CFDictionarySetValue(query, kSecValueData, value);
        CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly);
        status = SecItemAdd(query, NULL);
    }

    CFRelease(updates);
    CFRelease(value);
    CFRelease(query);
    return (int)status;
}

static inline int convx_keychain_clear(const char *account) {
    CFMutableDictionaryRef query = convx_keychain_query(account);
    if (query == NULL) return (int)errSecAllocate;
    OSStatus status = SecItemDelete(query);
    CFRelease(query);
    return (int)status;
}
