package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDto {
    private Long id;
    private Double startPoint;
    private Double endPoint;
    private Long parentParameterId;
    private String expression;
    private List<Long> relatedParameterIds;
}
