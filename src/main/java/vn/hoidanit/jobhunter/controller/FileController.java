package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.file.ResponseUploadFile;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.StorageException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {
      private final FileService fileService;
      @Value("${hoidanit.upload-file.base-uri}")
      private String baseUri;
      @Value("${hoidanit.upload-file.allowed-extensions}")
      private List<String> allowedExtensions;
      // Danh sách các loại file được phép dựa trên MIME Type
      private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
                  "application/pdf",
                  "image/jpeg",
                  "image/png",
                  "application/msword",
                  "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

      public FileController(FileService fileService) {
            this.fileService = fileService;
      }

      @PostMapping("/files")
      @ApiMessage("upload a file")
      public ResponseEntity<ResponseUploadFile> uploadFile(
                  @RequestParam(name = "file", required = false) MultipartFile file,
                  @RequestParam("folder") String folder)
                  throws URISyntaxException, IOException, StorageException {
            // TODO: process POST request

            // validate
            if (file == null || file.isEmpty()) {
                  throw new StorageException("file is empty");
            }
            // 2. Validation quan trọng: Kiểm tra MIME type của nội dung file
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());

            if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
                  throw new StorageException("Invalid file type. Only images, PDFs, and Word documents are allowed.");
            }
            String generatedFileName = fileService.store(file, folder);
            ResponseUploadFile responseUploadFile = new ResponseUploadFile(generatedFileName, Instant.now());
            return ResponseEntity.ok().body(responseUploadFile);

      }

      @GetMapping("/files")
      @ApiMessage("download a file")
      public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName,
                  @RequestParam("folder") String folder) throws StorageException {
            // 1. Lấy resource từ service (đã bao gồm các bước kiểm tra an toàn)
            Resource resource = fileService.loadAsResource(folder, fileName);

            // 2. Cố gắng xác định Content-Type của file
            String contentType = "application/octet-stream"; // Mặc định
            try {
                  // Lấy path từ resource để xác định MIME type
                  Path filePath = resource.getFile().toPath();
                  contentType = Files.probeContentType(filePath);
                  // Nếu Java không xác định được, giữ giá trị mặc định
                  if (contentType == null) {
                        contentType = "application/octet-stream";
                  }
            } catch (IOException ex) {
                  // Bỏ qua nếu không lấy được file, dùng contentType mặc định
                  throw new StorageException("Could not determine file type.");
            }

            // 3. Tạo và trả về response
            return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
      }

}
