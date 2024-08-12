package com.stylish.dao;

import com.stylish.model.Campaign;

import java.util.List;

public interface CampaignDao {
    int insertCampaign(Campaign campaign);
    List<Campaign> getCampaigns();
    Campaign getCampaignByProductId(int productId);
    void updateCampaign(Campaign campaign);
}