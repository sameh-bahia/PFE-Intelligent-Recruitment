package com.AppRecrutement.AppRecrutement.dto;

public class StatsDTO {
    private Long totalUsers;
    private Long totalOffres;
    private Long totalCandidatures;
    private Long totalEntretiens;

    public StatsDTO() {}

    public StatsDTO(Long totalUsers, Long totalOffres, Long totalCandidatures, Long totalEntretiens) {
        this.totalUsers = totalUsers;
        this.totalOffres = totalOffres;
        this.totalCandidatures = totalCandidatures;
        this.totalEntretiens = totalEntretiens;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalOffres() {
        return totalOffres;
    }

    public void setTotalOffres(Long totalOffres) {
        this.totalOffres = totalOffres;
    }

    public Long getTotalCandidatures() {
        return totalCandidatures;
    }

    public void setTotalCandidatures(Long totalCandidatures) {
        this.totalCandidatures = totalCandidatures;
    }

    public Long getTotalEntretiens() {
        return totalEntretiens;
    }

    public void setTotalEntretiens(Long totalEntretiens) {
        this.totalEntretiens = totalEntretiens;
    }
}
