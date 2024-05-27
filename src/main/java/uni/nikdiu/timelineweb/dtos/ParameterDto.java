package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.nikdiu.timelineweb.entities.Point;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {
    private Long id;
    private String name;
    private String description;
    private String abbreviation;
    private List<FunctionDto> functions;
    private List<PointDto> points;

}
