package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.nikdiu.timelineweb.models.Point;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineDto {
    private List<Point> points;
}
