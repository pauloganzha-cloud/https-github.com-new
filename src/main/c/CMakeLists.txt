cmake_minimum_required(VERSION 3.15)
project(lg3d-x11 C)

set(CMAKE_C_STANDARD 11)

# Find X11 libraries
find_package(X11 REQUIRED)
find_package(JNI REQUIRED)

# Include directories
include_directories(${X11_INCLUDE_DIR})
include_directories(${JAVA_INCLUDE_DIRS})

# Source files
set(SOURCES
    lg3d_x11.c
)

# Shared library
add_library(lg3d-x11 SHARED ${SOURCES})

# Link libraries
target_link_libraries(lg3d-x11
    ${X11_LIBRARIES}
    Xdamage
    Xcomposite
)

# Set output name
set_target_properties(lg3d-x11 PROPERTIES
    OUTPUT_NAME "lg3d-x11"
    PREFIX "lib"
)

# Installation
install(TARGETS lg3d-x11 LIBRARY DESTINATION lib)

message(STATUS "LG3D X11 Native Build")
message(STATUS "  X11 Include: ${X11_INCLUDE_DIR}")
message(STATUS "  JNI Include: ${JAVA_INCLUDE_DIRS}")