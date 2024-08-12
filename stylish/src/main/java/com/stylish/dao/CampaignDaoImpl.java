package com.stylish.dao;

import com.stylish.model.Campaign;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class CampaignDaoImpl implements CampaignDao {
    private final JdbcTemplate jdbcTemplate;

    public CampaignDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertCampaign(Campaign campaign) {
        String sql = "INSERT INTO campaigns (product_id, picture, story) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, campaign.getProductId());
            ps.setString(2, campaign.getPicture());
            ps.setString(3, campaign.getStory());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public List<Campaign> getCampaigns() {
        String sql = "SELECT c.*, p.title as product_title FROM campaigns c JOIN products p ON c.product_id = p.id";
        return jdbcTemplate.query(sql, this::mapRowToCampaign);
    }

    @Override
    public Campaign getCampaignByProductId(int productId) {
        String sql = "SELECT * FROM campaigns WHERE product_id = ?";
        List<Campaign> campaigns = jdbcTemplate.query(sql, this::mapRowToCampaign, productId);
        return campaigns.isEmpty() ? null : campaigns.get(0);
    }

    @Override
    public void updateCampaign(Campaign campaign) {
        String sql = "UPDATE campaigns SET picture = ?, story = ? WHERE product_id = ?";
        jdbcTemplate.update(sql, campaign.getPicture(), campaign.getStory(), campaign.getProductId());
    }

    private Campaign mapRowToCampaign(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Campaign campaign = new Campaign();
        campaign.setId(rs.getInt("id"));
        campaign.setProductId(rs.getInt("product_id"));
        campaign.setPicture(rs.getString("picture"));
        campaign.setStory(rs.getString("story"));
        return campaign;
    }
}