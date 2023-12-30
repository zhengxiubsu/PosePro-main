package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.CoordinateRepository;
import com.SCU.pose.repository.ImageRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class VideoService {

    @Autowired
    private PushUpService pushUpService;

    @Autowired
    private PlankService plankService;

    @Autowired
    private SitUpService sitUpService;

    @Autowired
    private GluteBridgeService gluteBridgeService;

    @Autowired
    private FlutterKicksService flutterKicksService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CoordinateRepository coordinateRepository;

    // Class attribute to store Image objects
    private List<Image> images = new ArrayList<>();



    // Counters for each type of exercise
    private static int pushUpCount = 0;
    private static int plankCount = 0;
    private static int sitUpCount = 0;
    private static int gluteBridgeCount = 0;
    private static int flutterKickCount = 0;

    // Method to process an image for a given exercise type and user
    public int processImage(Image image, String exerciseType, int userId) {
        if (image == null) {
            System.out.println("No image provided, skipping frame.");
            return 0; // Or return an error code indicating no processing was done.
        }
        int count = 0;

        switch (exerciseType) {
            case "pushup":
                count = pushUpService.analyzePushupFrame(image, userId);
                break;
            case "plank":
                count = plankService.analyzePlankFrame(image);
                plankCount += count; // increment plank count
                break;
            case "situp":
                count = sitUpService.analyzeSitUpFrame(image);
                sitUpCount += count; // increment sit-up count
                break;
            case "glutebridge":
                count = gluteBridgeService.analyzeGluteBridgeFrame(image);
                gluteBridgeCount += count; // increment glute bridge count
                break;
            case "flutterkick":
                count = flutterKicksService.analyzeFlutterKickFrame(image);
                flutterKickCount += count; // increment flutter kick count
                break;
            default:
                System.out.println("Unsupported exercise type: " + exerciseType);
                count = -9999; // indicate unsupported exercise type
        }
        return count;
    }








    public Image processSingleFrame(byte[] frameData) {
        List<Coordinate> coordinates = getCoordinatesFromFrame(frameData);
        if (coordinates.isEmpty()) {
            System.out.println("No pose detected, skipping frame.");
            return null;
        }
        Image image = new Image();
        image.setCoordinates(coordinates);
        imageRepository.save(image);
        return image;
    }




    private List<Coordinate> getCoordinatesFromFrame(byte[] frame) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://127.0.0.1:5000/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(frame) {
            @Override
            public String getFilename() {
                return "image.jpg"; // You can give any filename here
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

        // Check if response is OK
        if (response.getStatusCode() != HttpStatus.OK) {
            // Handle error response
            return new ArrayList<>();
        }

        // Parse the response to create Coordinate objects
        String responseBody = response.getBody();
        System.out.println("Response Body: " + responseBody);
        return parseCoordinates(responseBody);
    }

    private List<Coordinate> parseCoordinates(String json) {
        System.out.println("Parsing response: " + json);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Coordinate> coordinates = objectMapper.readValue(json, new TypeReference<List<Coordinate>>(){});
            String[] labels = {
                    "Nose", "Left Eye Inner", "Left Eye", "Left Eye Outer",
                    "Right Eye Inner", "Right Eye", "Right Eye Outer",
                    "Left Ear", "Right Ear", "Mouth Left", "Mouth Right",
                    "Left Shoulder", "Right Shoulder", "Left Elbow", "Right Elbow",
                    "Left Wrist", "Right Wrist", "Left Pinky", "Right Pinky",
                    "Left Index", "Right Index", "Left Thumb", "Right Thumb",
                    "Left Hip", "Right Hip", "Left Knee", "Right Knee",
                    "Left Ankle", "Right Ankle", "Left Heel", "Right Heel",
                    "Left Foot Index", "Right Foot Index"
            };
            for (int i = 0; i < coordinates.size(); i++) {
                coordinates.get(i).setKeyPointName(labels[i]);
                coordinateRepository.save(coordinates.get(i));
            }
            return coordinates;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<Image> processCoordinates() {
        List<Coordinate> allCoordinates = coordinateRepository.findAll(); // Assume this retrieves all coordinates


        List<Coordinate> tempCoordinates = new ArrayList<>();

        for (int i = 0; i < allCoordinates.size(); i++) {
            tempCoordinates.add(allCoordinates.get(i));

            // Every 33 coordinates, create a new Image object and save it
            if ((i + 1) % 33 == 0) {
                Image image = new Image();
                image.setCoordinates(new ArrayList<>(tempCoordinates));
                imageRepository.save(image);

                images.add(image);
                tempCoordinates.clear(); // Clear the temporary list for the next image
            }
        }

        // Handle remaining coordinates (if any)
        if (!tempCoordinates.isEmpty()) {
            Image image = new Image();
            image.setCoordinates(tempCoordinates);
            imageRepository.save(image);
            images.add(image);
        }
        return images;

        // You may choose to return the list of images or perform other operations
    }




    // Methods to get the current count for each exercise
    public int getPushUpCount() {
        return pushUpCount;
    }

    public int getPlankCount() {
        return plankCount;
    }

    public int getSitUpCount() {
        return sitUpCount;
    }

    public int getGluteBridgeCount() {
        return gluteBridgeCount;
    }

    public int getFlutterKickCount() {
        return flutterKickCount;
    }

    /**
     * ***********************
     * 以下是无用代码。
     * ***********************
     */


















    // Method to process video
    public String processVideo(byte[] videoBytes, int userId, String label) {
        // Find user by userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Invalid user";
        }

        // Extract key frames from the video bytes
        List<byte[]> keyFrames = extractKeyFrames(videoBytes);

        // Process each frame to create an Image object and analyze it
        for (byte[] frame : keyFrames) {
            List<Coordinate> coordinates = getCoordinatesFromFrame(frame);
            if (!coordinates.isEmpty()) {
                Image image = new Image();
                image.setCoordinates(coordinates);
                imageRepository.save(image);

                // Process the image for the given exercise type
                int result = processImage(image, label, userId);
                // You can now use the result as needed, e.g., accumulate it or store it
            }
        }

        return "Video processed successfully";
    }

    private List<byte[]> extractKeyFrames(byte[] videoBytes) {
        List<byte[]> keyFrames = new ArrayList<>();
        Java2DFrameConverter converter = new Java2DFrameConverter();

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(videoBytes))) {
            frameGrabber.start();

            int totalFrames = frameGrabber.getLengthInFrames();
            System.out.println("Total frames in video: " + totalFrames);

            if (totalFrames < 0) {
                System.err.println("Invalid total frame count.");
                return keyFrames; // or handle this case differently
            }

            for (int frameCount = 0; frameCount < totalFrames; frameCount++) {
                org.bytedeco.javacv.Frame frame = frameGrabber.grabImage();
                if (frame == null) {
                    continue;
                }

                // Extract a key frame every 3 frames
                if (frameCount % 100 == 0) {
                    BufferedImage bufferedImage = converter.convert(frame);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", baos);
                    keyFrames.add(baos.toByteArray());
                    System.out.println("Key frame extracted: " + frameCount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Extraction complete. Total key frames extracted: " + keyFrames.size());
        return keyFrames;
    }


}
