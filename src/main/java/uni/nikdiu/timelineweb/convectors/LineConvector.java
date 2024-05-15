package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.LineDto;
import uni.nikdiu.timelineweb.models.Line;

@Component
@NoArgsConstructor
public class LineConvector {
    public LineDto toDto(Line line) {
        return new LineDto(line.getPoints());
    }
}
