package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineDto {
    private List<Double> points = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    private String parameterName;
    private UnitDto unit;

    public LineDto(List<Double> points, List<String> labels, String parameterName) {
        this.points = points;
        this.labels = labels;
        this.parameterName = parameterName;
    }

    @Override
    public String toString() {
        return "LineDto " + parameterName +
                "\n     points: " + points +
                "\n     labels=" + labels
                ;
    }
}
