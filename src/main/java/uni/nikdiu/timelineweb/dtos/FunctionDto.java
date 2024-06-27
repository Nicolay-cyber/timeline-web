package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDto {
    private Long id;
    private Double startPoint;
    private Double endPoint;
    private Long parentParameterId;
    private String parentParameterName;
    private String expression;
    private String tagParamExpression;
    private List<Long> relatedParameterIds = new ArrayList<>();
}
