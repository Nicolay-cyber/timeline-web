package uni.nikdiu.timelineweb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    private String parameterName;
    private List<Double> points;
    private List<Double> labels;

    @Override
    public String toString() {
        return "Line " +
                "parameterName='" + parameterName + '\'' +
                ", points=" + points +
                ", labels=" + labels +
                '}';
    }
}
