package uni.nikdiu.timelineweb.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDto {
    private Long id;
    private Double startPoint;
    private Double endPoint;
    private String parentParameter;
    private String expression;
}
