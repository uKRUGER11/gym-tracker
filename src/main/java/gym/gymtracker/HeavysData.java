package gym.gymtracker;

public class HeavysData {
    private String exercise;
    private Double heavy;
    private Integer maxRep;
    private Integer minRep;
    private Integer userId;

    public HeavysData(String exercise, Double heavy, Integer maxRep, Integer minRep, Integer userId) {
        this.exercise = exercise;
        this.heavy = heavy;
        this.maxRep = maxRep;
        this.minRep = minRep;
        this.userId = userId;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public Double getHeavy() {
        return heavy;
    }

    public void setHeavy(Double heavy) {
        this.heavy = heavy;
    }

    public Integer getMaxRep() {
        return maxRep;
    }

    public void setMaxRep(Integer maxRep) {
        this.maxRep = maxRep;
    }

    public Integer getMinRep() {
        return minRep;
    }

    public void setMinRep(Integer minRep) {
        this.minRep = minRep;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
