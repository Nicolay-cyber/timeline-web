package uni.nikdiu.timelineweb.convectors;

import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.PointDto;
import uni.nikdiu.timelineweb.entities.Point;

@Component

public class PointConvector {
    public PointDto toDto(Point point){
        return new PointDto(point.getId(), point.getX(), point.getY());
    }
}
