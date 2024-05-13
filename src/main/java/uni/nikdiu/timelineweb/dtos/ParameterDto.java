package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {
    private Long id;
    private String name;
    private String description;
    private String abbreviation;
    private List<String> functions;

}
