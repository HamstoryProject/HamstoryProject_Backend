package com.codingrecipe.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class FirebaseService {

    String firebaseBucket = "hamstory-32e2f.appspot.com";

    public String uploadFile(MultipartFile file, String filename){
        try {
            Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
            InputStream content = new ByteArrayInputStream(file.getBytes());
            bucket.create(filename, content, file.getContentType());
            return createAccessUrl(filename);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String createAccessUrl(String filename) {
        return "https://firebasestorage.googleapis.com/v0/b/hamstory-32e2f.appspot.com/o/" + filename.replace("/", "%2F") + "?alt=media";
    }
}
