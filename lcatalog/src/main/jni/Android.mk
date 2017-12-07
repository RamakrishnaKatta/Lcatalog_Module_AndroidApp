
MY_LOCAL_PATH := $(call my-dir)
LOCAL_PATH := $(MY_LOCAL_PATH)

# Pull ARWrapper and any other required libs into the build
include $(CLEAR_VARS)
ARTOOLKIT_DIR := $(MY_LOCAL_PATH)/../../../../../../android
ARTOOLKIT_LIBDIR := $(call host-path, $(ARTOOLKIT_DIR)/obj/local/$(TARGET_ARCH_ABI))
ARTOOLKIT_LIBDIR2 := $(call host-path, $(ARTOOLKIT_DIR)/libs/$(TARGET_ARCH_ABI))

define add_static_module
	include $(CLEAR_VARS)
	LOCAL_MODULE:=$1
	LOCAL_SRC_FILES:=lib$1.a
	include $(PREBUILT_STATIC_LIBRARY)
endef

define add_shared_module
	include $(CLEAR_VARS)
	LOCAL_MODULE:=$1
	LOCAL_SRC_FILES:=lib$1.so
	include $(PREBUILT_SHARED_LIBRARY)
endef

MY_STATIC_LIBS := eden
LOCAL_PATH := $(ARTOOLKIT_LIBDIR)
$(foreach module,$(MY_STATIC_LIBS),$(eval $(call add_static_module,$(module))))

MY_SHARED_LIBS := ARWrapper
LOCAL_PATH := $(ARTOOLKIT_LIBDIR2)
$(foreach module,$(MY_SHARED_LIBS),$(eval $(call add_shared_module,$(module))))

LOCAL_PATH := $(MY_LOCAL_PATH)

# Android arvideo depends on CURL.
CURL_DIR := $(ARTOOLKIT_DIR)/jni/curl
CURL_LIBDIR := $(call host-path, $(CURL_DIR)/libs/$(TARGET_ARCH_ABI))
define add_curl_module
	include $(CLEAR_VARS)
	LOCAL_MODULE:=$1
	#LOCAL_SRC_FILES:=lib$1.so
	#include $(PREBUILT_SHARED_LIBRARY)
	LOCAL_SRC_FILES:=lib$1.a
	include $(PREBUILT_STATIC_LIBRARY)
endef

#CURL_LIBS := curl ssl crypto
CURL_LIBS := curl
LOCAL_PATH := $(CURL_LIBDIR)
$(foreach module,$(CURL_LIBS),$(eval $(call add_curl_module,$(module))))

LOCAL_PATH := $(MY_LOCAL_PATH)
include $(CLEAR_VARS)

# ARToolKit libs use lots of floating point, so don't compile in thumb mode.

LOCAL_ARM_MODE := arm
LOCAL_PATH := $(MY_LOCAL_PATH)
LOCAL_MODULE := ARNativeWrapper
LOCAL_SRC_FILES := ARNativeWrapper.cpp

# Make sure DEBUG is defined for debug builds. (NDK already defines NDEBUG for release builds.)
ifeq ($(APP_OPTIM),debug)
    LOCAL_CPPFLAGS += -DDEBUG
endif

LOCAL_C_INCLUDES += $(ARTOOLKIT_DIR)/../include/android $(ARTOOLKIT_DIR)/../include
LOCAL_LDLIBS += -llog -lGLESv1_CM
LOCAL_STATIC_LIBRARIES += eden
LOCAL_SHARED_LIBRARIES += ARWrapper
#LOCAL_SHARED_LIBRARIES += $(CURL_LIBS)

include $(BUILD_SHARED_LIBRARY)
