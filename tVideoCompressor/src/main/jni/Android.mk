LOCAL_PATH := $(call my-dir)



include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE 	:= tmessages.7
LOCAL_CFLAGS 	:= -w -std=gnu99 -O2 -DNULL=0 -DSOCKLEN_T=socklen_t -DLOCALE_NOT_USED -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CFLAGS 	+= -Drestrict='' -D__EMX__ -DOPUS_BUILD -DFIXED_POINT -DUSE_ALLOCA -DHAVE_LRINT -DHAVE_LRINTF -fno-math-errno
LOCAL_CFLAGS 	+= -DANDROID_NDK -DDISABLE_IMPORTGL -fno-strict-aliasing -fprefetch-loop-arrays -DAVOID_TABLES -DANDROID_TILE_BASED_DECODE -DANDROID_ARMV6_IDCT -ffast-math
LOCAL_CPPFLAGS 	:= -DBSD=1 -ffast-math -O2 -funroll-loops
LOCAL_LDLIBS 	:= -ljnigraphics -llog
ifeq ($(TARGET_ARCH_ABI),armeabi)
	LOCAL_ARM_MODE  := thumb
else
	LOCAL_ARM_MODE  := arm
endif

LOCAL_C_INCLUDES    := \

./libyuv/include


LOCAL_SRC_FILES     += \
./libyuv/source/compare_common.cc \
./libyuv/source/compare_neon.cc \
./libyuv/source/compare_posix.cc \
./libyuv/source/compare_win.cc \
./libyuv/source/compare.cc \
./libyuv/source/convert_argb.cc \
./libyuv/source/convert_from_argb.cc \
./libyuv/source/convert_from.cc \
./libyuv/source/convert_jpeg.cc \
./libyuv/source/convert_to_argb.cc \
./libyuv/source/convert_to_i420.cc \
./libyuv/source/convert.cc \
./libyuv/source/cpu_id.cc \
./libyuv/source/format_conversion.cc \
./libyuv/source/mjpeg_decoder.cc \
./libyuv/source/mjpeg_validate.cc \
./libyuv/source/planar_functions.cc \
./libyuv/source/rotate_argb.cc \
./libyuv/source/rotate_mips.cc \
./libyuv/source/rotate_neon.cc \
./libyuv/source/rotate_neon64.cc \
./libyuv/source/rotate.cc \
./libyuv/source/row_any.cc \
./libyuv/source/row_common.cc \
./libyuv/source/row_mips.cc \
./libyuv/source/row_neon.cc \
./libyuv/source/row_neon64.cc \
./libyuv/source/row_posix.cc \
./libyuv/source/row_win.cc \
./libyuv/source/scale_argb.cc \
./libyuv/source/scale_common.cc \
./libyuv/source/scale_mips.cc \
./libyuv/source/scale_neon.cc \
./libyuv/source/scale_neon64.cc \
./libyuv/source/scale_posix.cc \
./libyuv/source/scale_win.cc \
./libyuv/source/scale.cc \
./libyuv/source/video_common.cc

LOCAL_SRC_FILES     += \
./jni.c \

./audio.c \

./utils.c \

./video.c

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/cpufeatures)