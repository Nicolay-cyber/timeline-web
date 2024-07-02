package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullModelDto {
    private Long id;
    private String name;
    private String description;
    private List<ParameterDto> parameters = new ArrayList<>();
    private List<Double> XValues = new ArrayList<>();
    private List<LineDto> YValues = new ArrayList<>();
}
