#
# Tests
#
# Tests are executed only once during build. If no source files updated - no test will be executed.
#
# Tests are run on the targed device, and will fail, if device is not connected.
# If required to build project without installing, tests should be disabled.
#
# Tests are enabled by default.
# To disable tests add line "enable_cpp_tests=false" to the file "local.properties".
#

if (${ENABLE_CPP_TESTS} MATCHES "true")

    # Build and link tests

    # Path to Google Test source dir included into Android NDK.
    # Note: Another Google Test implementation can be used.

    set(GOOGLETEST_ROOT ${ANDROID_NDK}/sources/third_party/googletest)

    add_library(gtest STATIC ${GOOGLETEST_ROOT}/src/gtest_main.cc ${GOOGLETEST_ROOT}/src/gtest-all.cc)
    target_include_directories(gtest PRIVATE ${GOOGLETEST_ROOT})
    target_include_directories(gtest PUBLIC ${GOOGLETEST_ROOT}/include/)
    add_executable(yfble-test
            ${TEST_SRC_DIR}/bleTest.cc
            )
    target_link_libraries(yfble-test yfble gtest)

    # Push and execute tests as post-build event.

    set(TARGET_TEST_DIR /data/local/tmp/yfble-test) # Directory on device to push tests.
    set(TARGET_TEST_LIB_DIR ${TARGET_TEST_DIR}/${ANDROID_ABI})
    message("ANDROID_SDK_ROOT: ${ANDROID_SDK_ROOT}")

    find_program(ADB NAMES adb PATHS ${ANDROID_SDK_ROOT}/platform-tools) # Verified to be working on Linux.

    add_custom_command(TARGET yfble-test POST_BUILD
            COMMAND ${ADB} shell mkdir -p ${TARGET_TEST_LIB_DIR}

            # Push libraries

            COMMAND ${ADB} push $<TARGET_FILE:yfble-test> ${TARGET_TEST_LIB_DIR}/
            COMMAND ${ADB} push $<TARGET_FILE:yfble> ${TARGET_TEST_LIB_DIR}/

            # Execute tests
            COMMAND ${ADB} shell chmod +x ${TARGET_TEST_LIB_DIR}/yfble-test /

            # FIXME What's correct: Build is stopped, if tests failed.
            # What's to fix: Next build, if no files changed, will not execute and tests will not run, i.e. untested apk will be installed.
            # Note: Sometimes desired logic is working. Sometimes not.
            COMMAND ${ADB} shell \"export LD_LIBRARY_PATH=${TARGET_TEST_LIB_DIR}\; ${TARGET_TEST_LIB_DIR}/yfble-test\")

endif () # ENABLE_CPP_TESTS