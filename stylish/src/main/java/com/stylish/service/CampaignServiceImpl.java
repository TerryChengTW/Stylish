package com.stylish.service;

import com.stylish.dao.CampaignDao;
import com.stylish.model.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CampaignServiceImpl implements CampaignService {

    private static final String BASE_URL = "http://18.182.90.188";
    private static final String CACHE_KEY_PREFIX = "campaign:";
    private static final int CACHE_TTL = 60;
    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

    private final CampaignDao campaignDao;
    private final FileUploadService fileUploadService;
    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisHealthService redisHealthService;

    public CampaignServiceImpl(CampaignDao campaignDao, FileUploadService fileUploadService,
                               ProductService productService,
                               RedisTemplate<String, Object> redisTemplate,
                               RedisHealthService redisHealthService) {
        this.campaignDao = campaignDao;
        this.fileUploadService = fileUploadService;
        this.productService = productService;
        this.redisTemplate = redisTemplate;
        this.redisHealthService = redisHealthService;
    }

    @Override
    public Campaign addOrUpdateCampaign(int productId, String story, MultipartFile pictureFile) throws Exception {
        Campaign existingCampaign = campaignDao.getCampaignByProductId(productId);

        if (existingCampaign == null) {
            return addCampaign(productId, story, pictureFile);
        } else {
            updateCampaign(existingCampaign, story, pictureFile);
            return existingCampaign;
        }
    }

    public Campaign addCampaign(int productId, String story, MultipartFile pictureFile) throws Exception {
        Map<String, Object> productDetails = productService.getProductDetails(productId);
        if (productDetails == null) {
            throw new IllegalArgumentException("Product not found");
        }

        String imageUrl;
        if (pictureFile == null || pictureFile.isEmpty()) {
            imageUrl = (String) productDetails.get("main_image");
            imageUrl = imageUrl.replace(BASE_URL, "");
        } else {
            imageUrl = fileUploadService.saveImage(pictureFile);
        }

        Campaign campaign = new Campaign();
        campaign.setProductId(productId);
        campaign.setStory(story);
        campaign.setPicture(imageUrl);

        int campaignId = campaignDao.insertCampaign(campaign);
        campaign.setId(campaignId);

        return campaign;
    }

    private void updateCampaign(Campaign existingCampaign, String story, MultipartFile pictureFile) throws Exception {
        Map<String, Object> productDetails = productService.getProductDetails(existingCampaign.getProductId());
        if (productDetails == null) {
            throw new IllegalArgumentException("Product not found");
        }

        String imageUrl;
        if (pictureFile == null || pictureFile.isEmpty()) {
            imageUrl = existingCampaign.getPicture();
        } else {
            imageUrl = fileUploadService.saveImage(pictureFile);
        }

        existingCampaign.setStory(story);
        existingCampaign.setPicture(imageUrl);

        campaignDao.updateCampaign(existingCampaign);

        evictCampaignCache(existingCampaign.getProductId());
    }

    private void evictCampaignCache(int productId) {
        String cacheKey = CACHE_KEY_PREFIX + productId;
        redisTemplate.delete(cacheKey);
    }

    @Override
    public List<Map<String, Object>> getCampaigns() {
        logger.info("Getting campaigns");

        if (redisHealthService.isRedisJustReconnected()) {
            logger.info("Redis just reconnected. Clearing all campaign cache.");
            clearRedisCache();
            redisHealthService.checkAndSetRedisStatus(true);
        }

        List<Campaign> campaigns = campaignDao.getCampaigns();
        List<Map<String, Object>> campaignData = new ArrayList<>();

        if (redisHealthService.isRedisOffline()) {
            logger.info("Redis is offline. Retrieving all campaigns from database.");
            for (Campaign campaign : campaigns) {
                Map<String, Object> campaignMap = getCampaignFromDatabase(campaign);
                campaignData.add(campaignMap);
            }
        } else {
            for (Campaign campaign : campaigns) {
                String cacheKey = CACHE_KEY_PREFIX + campaign.getProductId();
                logger.info("Checking cache for key: {}", cacheKey);

                Map<String, Object> campaignMap = null;
                try {
                    Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
                    if (cachedObject instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> cachedCampaign = (Map<String, Object>) cachedObject;
                        logger.info("Campaign {} retrieved from cache", campaign.getProductId());
                        campaignMap = cachedCampaign;
                    }
                } catch (Exception e) {
                    logger.error("Error accessing Redis cache", e);
                    redisHealthService.setRedisOffline(true);
                }

                if (campaignMap == null) {
                    logger.info("Campaign {} not found in cache, retrieving from database", campaign.getProductId());
                    campaignMap = getCampaignFromDatabase(campaign);
                    try {
                        redisTemplate.opsForValue().set(cacheKey, campaignMap, CACHE_TTL, TimeUnit.SECONDS);
                        logger.info("Stored campaign {} in cache", campaign.getProductId());
                    } catch (Exception e) {
                        logger.error("Error storing campaign {} in cache", campaign.getProductId(), e);
                        redisHealthService.setRedisOffline(true);
                    }
                }

                campaignData.add(campaignMap);
            }
        }
        return campaignData;
    }

    private Map<String, Object> getCampaignFromDatabase(Campaign campaign) {
        Map<String, Object> campaignMap = new HashMap<>();
        campaignMap.put("product_id", campaign.getProductId());
        campaignMap.put("picture", BASE_URL + campaign.getPicture());
        campaignMap.put("story", campaign.getStory());
        return campaignMap;
    }

    private void clearRedisCache() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            logger.info("Cleared all campaign cache keys");
        } catch (Exception e) {
            logger.error("Error clearing Redis cache", e);
        }
    }
}