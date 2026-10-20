cmake_minimum_required(VERSION 3.22.1)
project(convx_analysis LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(convx_analysis SHARED
    audio_analysis.cpp
    tempo_analysis.cpp
    resampler.cpp
    mel_spectrogram.cpp
    vocal_spectrogram.cpp
    jni/analysis_jni.cpp
    jni/mel_jni.cpp
    jni/vocal_jni.cpp
)

target_include_directories(convx_analysis PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    # jni/*.cpp include their siblings as "analyzer/foo.h" (this module's own
    # name as a prefix), while the root-level .cpp files include them bare —
    # the parent dir satisfies the former without breaking the latter.
    ${CMAKE_CURRENT_SOURCE_DIR}/..
)

# -ffast-math is deliberately NOT enabled: the analyzer's log/mel-adjacent
# arithmetic (see audio_analysis.cpp) is tuned against exact IEEE behaviour.
target_compile_options(convx_analysis PRIVATE -O3 -fvisibility=hidden)

find_library(log-lib log)
target_link_libraries(convx_analysis ${log-lib} c++_static c++abi)

# Required for Android's 16 KB page size devices — without this the linker
# defaults to 4 KB ELF segment alignment and the loader refuses/degrades the
# library at runtime (surfaces as a silent System.loadLibrary failure, not a
# crash, since callers wrap it in runCatching).
target_link_options(convx_analysis PRIVATE "-Wl,-z,max-page-size=16384")
