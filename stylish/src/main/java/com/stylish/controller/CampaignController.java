package com.stylish.controller;

import com.stylish.model.Campaign;
import com.stylish.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/marketing")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/campaigns")
    public ResponseEntity<?> addOrUpdateCampaign(@RequestParam(value = "productId") Integer productId,
                                                 @RequestParam(value = "story") String story,
                                                 @RequestParam(value = "picture", required = false) MultipartFile pictureFile) {
        try {
            Campaign campaign = campaignService.addOrUpdateCampaign(productId, story, pictureFile);
            String message = campaign.getId() == 0 ? "Campaign updated successfully" : "Campaign added successfully";
            return ResponseEntity.ok(Map.of("message", message, "id", campaign.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Required request parameter '" + paramName + "' is not present");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<Map<String, Object>> getCampaigns() {
        try {
            List<Map<String, Object>> campaignData = campaignService.getCampaigns();
            Map<String, Object> response = new HashMap<>();
            response.put("data", campaignData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }
}