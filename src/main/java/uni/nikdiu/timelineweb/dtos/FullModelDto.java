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

    private ModelGraphDto modelGraphDto;
    public FullModelDto(Long id, String name, String description, List<ParameterDto> parameters) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
}
