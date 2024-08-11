package uni.nikdiu.timelineweb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    private String parameterName;
    private List<Double> points= new ArrayList<>();
    private List<Double> labels= new ArrayList<>();


    @Override
    public String toString() {
        return "Line " +
                "parameterName='" + parameterName + '\'' +
                ", points=" + points +
                ", labels=" + labels +
                '}';
    }
}
