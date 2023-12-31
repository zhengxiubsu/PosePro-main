package com.SCU.pose.controller;
import com.SCU.pose.model.Image;
import com.SCU.pose.model.Video;
import com.SCU.pose.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private PushUpService pushUpService;
    @Autowired
    private PlankService plankService;
    @Autowired
    private FlutterKicksService flutterKickService;
    @Autowired
    private GluteBridgeService gluteBridgeService;
    @Autowired
    private SitUpService sitUpService;








    /**
     * Endpoint to create a new video session.
     *
     * @param userId The ID of the user starting the exercise session.
     * @param exerciseType The type of exercise.
     * @return The ID of the created video session.
     */
    @PostMapping("/create")
    public ResponseEntity<Integer> createVideoSession(@RequestParam("userId") int userId,
                                                      @RequestParam("exerciseType") String exerciseType) {
        try {
            Video video = videoService.createAndSaveVideo(userId, exerciseType);
            return ResponseEntity.ok(video.getId()); // Return the ID of the created video
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }





    /**
     * Endpoint to process a single video frame for different exercises.
     *
     * @param frame The video frame to process.
     * @param userId The ID of the user performing the exercise.
     * @param exerciseType The type of exercise to analyze.
     * @return Returns analysis result such as count.
     */
    @PostMapping("/process-frame")
    public ResponseEntity<Integer> processVideoFrame(@RequestParam("frame") MultipartFile frame,
                                                     @RequestParam("videoId") int videoId, //
                                                     @RequestParam("userId") int userId,
                                                     @RequestParam("exerciseType") String exerciseType) {
        try {
            // Convert MultipartFile to byte array
            byte[] frameData = frame.getBytes();

            // Process the frame to get Image object
            Image image = videoService.processSingleFrame(frameData);

            // Associate the processed frame with the specified video session
            videoService.addFrameToVideo(videoId, image);

            // Process the image for the specified exercise type
            int count = videoService.processImage(image, exerciseType, userId,videoId);

            // Return the count for the processed frame
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1);
        }
    }













//    @GetMapping("/process-frame") // Changed to GET as we are not uploading a file
//    public int processVideoFrame(@RequestParam("userId") int userId,
//                                 @RequestParam("exerciseType") String exerciseType) {
//
//
//        // Process the frame data to create Image objects
//        List<Image> images = videoService.processCoordinates();
//
//        // Initialize total count
//        int totalCount = 0;
//
//        // Loop through each image and process it
//        for (Image image : images) {
//            int count = videoService.processImage(image, exerciseType, userId);
//            // Accumulate the count
//            totalCount += count;
//            System.out.println("Total count for " + exerciseType + ": " + totalCount);
//        }
//
//        // Return the total count for the processed frame
//        return totalCount;
//    }
//













    private byte[] extractFrameData(MultipartFile file) {
        // Implement logic to extract frame data from MultipartFile
        return new byte[0];
    }
}