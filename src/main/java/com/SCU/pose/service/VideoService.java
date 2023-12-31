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
import java.util.Optional;

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





    //**********************************12.31**********************************
    public Video createAndSaveVideo(int userId, String exerciseType) {
        Video video = new Video();
        video.setLabel(exerciseType);

        // Find the user by userId and set it in the Video object
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        video.setUser(user);

        // Initialize an empty list of images for the video
        video.setImages(new ArrayList<>());

        videoRepository.save(video);
        return video;
    }



    //**********************************12.31**********************************
    /**
     * Adds a processed frame (Image object) to a specific Video entity.
     *
     * @param videoId The ID of the Video to which the frame will be added.
     * @param image The Image object representing the processed frame.
     */
    public void addFrameToVideo(int videoId, Image image) {
        // Fetch the Video entity based on the provided videoId.
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();

            // Retrieve the current list of images (frames) associated with the video.
            List<Image> images = video.getImages();
            if (images == null) {
                // Initialize the list if it's null.
                images = new ArrayList<>();
                video.setImages(images);
            }
            // Add the new image to the list.
            images.add(image);

            // Save the updated Video entity back to the database.
            videoRepository.save(video);
        } else {
            // Handle the case where no corresponding Video entity is found.
            throw new RuntimeException("Video not found with ID: " + videoId);
        }
    }







    // Method to process an image for a given exercise type and user
    public int processImage(Image image, String exerciseType, int userId, int videoId) {
        if (image == null) {
            System.out.println("No image provided, returning current count.");

            return getCurrentCountForExerciseType(exerciseType, userId, videoId);
        }
        int count=0;
        switch (exerciseType) {
            case "pushup":
                count = pushUpService.analyzePushupFrame(image, userId, videoId);
                break;
//            case "plank":
//                count = plankService.analyzePlankFrame(image, userId, videoId);
//                // increment plank count
//                break;
//            case "situp":
//                count = sitUpService.analyzeSitUpFrame(image, userId, videoId);
//                 // increment sit-up count
//                break;
//            case "glutebridge":
//                count = gluteBridgeService.analyzeGluteBridgeFrame(image, userId, videoId);
//                 // increment glute bridge count
//                break;
//            case "flutterkick":
//                count = flutterKicksService.analyzeFlutterKickFrame(image, userId, videoId);
//                 // increment flutter kick count
//                break;
            default:
                System.out.println("Unsupported exercise type: " + exerciseType);
                count = -9999; // indicate unsupported exercise type
        }
        return count;
    }



    private int getCurrentCountForExerciseType(String exerciseType, int userId, int videoId) {
        switch (exerciseType) {
            case "pushup":
                // 获取 pushup 的当前计数
                return pushUpService.getCurrentPushUpCount( videoId);
            // .
            default:
                return 0; //
        }
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




    /**
     * ***********************
     * 以下是无用代码。
     * ***********************
     */




















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
