package com.e_commerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // file name of original / current file
        String originalfilename=file.getOriginalFilename();
        //Generate a unique file name
        String randomId= UUID.randomUUID().toString();
        // mat.jpg --> 1234.jpg
        String filename=randomId.concat(originalfilename.substring(originalfilename.lastIndexOf('.')));
        String filepath=path+ File.separator+filename;   // path+"/"+filename

        //check if path exist and create
        File folder=new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        // Upload to the server
        Files.copy(file.getInputStream(), Paths.get(filepath));
        return filename;

    }
}
