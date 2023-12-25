package com.bee.chat.util;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileUtil {
    private final ResourceLoader resourceLoader;

    public Resource getFileInClasspath(String path) {
        String filepath = "classpath:" + path;
        return this.resourceLoader.getResource(filepath);
    }
}
