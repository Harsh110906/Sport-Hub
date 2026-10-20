package com.example.sporthub.models;

public class Match {
    private int id;
    private int tournamentId;
    private int teamAId;
    private int teamBId;
    private String matchDate;
    private String venue;
    private String status;
    private Integer scoreA;
    private Integer scoreB;

    // Transient fields for display purposes
    private String teamAName;
    private String teamBName;
    private String tournamentName;

    public Match() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public int getTeamAId() { return teamAId; }
    public void setTeamAId(int teamAId) { this.teamAId = teamAId; }

    public int getTeamBId() { return teamBId; }
    public void setTeamBId(int teamBId) { this.teamBId = teamBId; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getScoreA() { return scoreA; }
    public void setScoreA(Integer scoreA) { this.scoreA = scoreA; }

    public Integer getScoreB() { return scoreB; }
    public void setScoreB(Integer scoreB) { this.scoreB = scoreB; }

    public String getTeamAName() { return teamAName; }
    public void setTeamAName(String teamAName) { this.teamAName = teamAName; }

    public String getTeamBName() { return teamBName; }
    public void setTeamBName(String teamBName) { this.teamBName = teamBName; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
}