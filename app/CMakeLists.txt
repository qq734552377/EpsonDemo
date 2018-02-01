# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)



add_library(
            serial_port_c
            SHARED
            src/main/cpp/serial_port.c )


find_library(
              log-lib
              log )




target_link_libraries(

                       serial_port_c

                       ${log-lib} )