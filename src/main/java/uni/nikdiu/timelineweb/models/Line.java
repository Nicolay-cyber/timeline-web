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
    private List<Point> points;
}
