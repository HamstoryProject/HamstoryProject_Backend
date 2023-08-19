package com.codingrecipe.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FirebaseService {

    String firebaseBucket = "hamstory-32e2f.appspot.com";

    public String uploadFile(MultipartFile file, String path){
        try {
            String filename = path + UUID.randomUUID();
            if(file == null) {
                return null;
            }
            Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
            InputStream content = new ByteArrayInputStream(file.getBytes());
            bucket.create(filename, content, file.getContentType());
            return createAccessUrl(filename);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<String> uploadAll(List<MultipartFile> files, String path) {
        try {
            if(files != null) {
                List<String> urlList = new ArrayList<>();
                for(MultipartFile file : files) {
                    if(!file.isEmpty()) {
                        String url = uploadFile(file, path);
                        if(url != null) {
                            urlList.add(url);
                        }
                    }
                }
                return urlList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFile(String fileUrl) {
        try {
            if(fileUrl == null) {
                return;
            }
            Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
            String filename = getFilename(fileUrl);
            bucket.get(filename).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAll(List<String> fileList) {
        try {
            if(fileList != null) {
                for(String url : fileList) {
                    deleteFile(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createAccessUrl(String filename) {
        return "https://firebasestorage.googleapis.com/v0/b/hamstory-32e2f.appspot.com/o/" + filename.replace("/", "%2F") + "?alt=media";
    }

    private String getFilename(String fileUrl) {
        return fileUrl.replace("https://firebasestorage.googleapis.com/v0/b/hamstory-32e2f.appspot.com/o/", "")
                .replace("?alt=media", "")
                .replace("%2F", "/");
    }
}
