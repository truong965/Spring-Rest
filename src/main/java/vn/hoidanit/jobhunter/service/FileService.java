package vn.hoidanit.jobhunter.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import vn.hoidanit.jobhunter.util.error.StorageException;

@Service
public class FileService {
      @Value("${hoidanit.upload-file.base-uri}")
      private String baseUri;
      private static final Logger log = LoggerFactory.getLogger(FileService.class);
      private Path rootLocation;

      @PostConstruct
      public void init() throws StorageException {
            // Khởi tạo đường dẫn gốc từ baseUri
            this.rootLocation = Paths.get(baseUri);
            try {
                  Files.createDirectories(rootLocation);
                  log.info("Storage location initialized: {}", rootLocation.toAbsolutePath());
            } catch (IOException e) {
                  log.error("Could not initialize storage location", e);
                  throw new StorageException("Could not initialize storage location", e);
            }
      }

      public String store(MultipartFile file, String folder) throws URISyntaxException,
                  IOException, StorageException {
            // 1. Chuẩn hóa tên file
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                  throw new StorageException("File name is invalid.");
            }
            // 2. Kiểm tra Path Traversal trong tên folder
            if (folder.contains("..")) {
                  throw new StorageException(
                              "Cannot store file with relative path outside current directory " + folder);
            }
            try {
                  // 3. Tạo tên file duy nhất và an toàn
                  String extension = FilenameUtils.getExtension(originalFilename);
                  String finalFilename = UUID.randomUUID().toString() + "." + extension;

                  // 4. Tạo đường dẫn lưu file tuyệt đối
                  Path folderPath = this.rootLocation.resolve(folder);
                  Files.createDirectories(folderPath); // Tạo thư mục nếu chưa tồn tại

                  Path destinationFile = folderPath.resolve(finalFilename).normalize().toAbsolutePath();

                  // 5. Kiểm tra lần cuối để chắc chắn file không nằm ngoài thư mục gốc
                  if (!destinationFile.getParent().startsWith(this.rootLocation.toAbsolutePath())) {
                        throw new StorageException("Cannot store file outside current directory.");
                  }

                  // 6. Copy file vào thư mục đích
                  try (InputStream inputStream = file.getInputStream()) {
                        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                  }

                  log.info("Stored file successfully: {}", finalFilename);
                  return finalFilename;

            } catch (IOException e) {
                  log.error("Failed to store file.", e);
                  throw new StorageException("Failed to store file.", e);
            }
      }

      public Resource loadAsResource(String folder, String fileName) throws StorageException {
            try {
                  // 1. Kiểm tra Path Traversal trong folder và fileName
                  if (folder.contains("..") || fileName.contains("..")) {
                        throw new StorageException("Cannot access files with relative path.");
                  }

                  // 2. Tạo đường dẫn đầy đủ và an toàn đến file
                  Path file = rootLocation.resolve(folder).resolve(fileName).normalize();

                  // 3. Kiểm tra lần cuối để đảm bảo file nằm trong thư mục gốc
                  if (!file.startsWith(this.rootLocation)) {
                        throw new StorageException("Cannot access files outside the storage directory.");
                  }

                  Resource resource = new UrlResource(file.toUri());

                  // 4. Kiểm tra xem file có tồn tại và đọc được không
                  if (resource.exists() && resource.isReadable()) {
                        return resource;
                  } else {
                        throw new StorageException("Could not read file: " + fileName);
                  }
            } catch (MalformedURLException e) {
                  throw new StorageException("Could not read file: " + fileName, e);
            }
      }
}
