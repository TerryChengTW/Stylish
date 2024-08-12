package com.stylish.service;

import com.stylish.model.Campaign;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CampaignService {
    Campaign addOrUpdateCampaign(int productId, String story, MultipartFile pictureFile) throws Exception;
    List<Map<String, Object>> getCampaigns();
}