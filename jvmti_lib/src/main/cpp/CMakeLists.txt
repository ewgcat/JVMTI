cmake_minimum_required(VERSION 3.4.1)


add_library(
        monitor_agent
        SHARED
        monitor.cpp
        MemoryFile.cpp)


target_link_libraries(
        monitor_agent
        log)