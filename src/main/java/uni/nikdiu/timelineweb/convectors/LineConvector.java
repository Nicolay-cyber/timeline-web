package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.LineDto;
import uni.nikdiu.timelineweb.models.Line;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class LineConvector {

    public LineDto toDto(Line line) {
        List<String> labels = line.getLabels().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        return new LineDto(line.getPoints(), labels, line.getParameterName());
    }
}
